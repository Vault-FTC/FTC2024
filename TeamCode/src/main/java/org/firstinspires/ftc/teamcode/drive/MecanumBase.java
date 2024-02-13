package org.firstinspires.ftc.teamcode.drive;

import android.util.Pair;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.PIDController;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MecanumBase {
    public final DcMotor lf;
    public final DcMotor rf;
    public final DcMotor lb;
    public final DcMotor rb;

    private int waypointIndex = 0;

    private Path followPath;
    private final ElapsedTime timer = new ElapsedTime();
    private double followStartTimestamp;
    private Waypoint lastWaypoint = null;
    private Waypoint[][] segments;

    public enum DriveState {
        DRIVE,
        FOLLOWING,
        END_PATH

    }

    public DriveState driveState = DriveState.DRIVE;

    private final Supplier<Pose2d> poseSupplier;
    public PIDController driveController = new PIDController(0.1, 0, 0);

    public PIDController rotController = new PIDController(0.1, 0, 0);

    public MecanumBase(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack, Supplier<Pose2d> poseSupplier) {
        lf = leftFront;
        rf = rightFront;
        lb = leftBack;
        rb = rightBack;
        this.poseSupplier = poseSupplier;
        timer.startTime();
    }

    public MecanumBase(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack) {
        this(leftFront, rightFront, leftBack, rightBack, () -> new Pose2d());
    }

    private void drive(double drive, double strafe, double turn, double botHeading, boolean squareInputs) {
        if (squareInputs) {
            drive = Math.copySign(Math.pow(drive, 2), drive);
            strafe = Math.copySign(Math.pow(strafe, 2), strafe);
            turn = Math.copySign(Math.pow(turn, 2), turn);
        }
        double originalDrive = drive;
        drive = strafe * Math.sin(-botHeading) + drive * Math.cos(-botHeading);
        strafe = strafe * Math.cos(-botHeading) - originalDrive * Math.sin(-botHeading);
        strafe *= 1.41;

        double[] wheelSpeeds = { // Order: lf, rf, lb, rb
                drive + strafe - turn,
                drive - strafe + turn,
                drive - strafe - turn,
                drive + strafe + turn
        };

        double largest = 1.0;

        for (double wheelSpeed : wheelSpeeds) {
            if (Math.abs(wheelSpeed) > largest) {
                largest = Math.abs(wheelSpeed);
            }
        }
        for (int i = 0; i < wheelSpeeds.length; i++) {
            wheelSpeeds[i] /= largest;
        }

        lf.setPower(wheelSpeeds[0]);
        rf.setPower(wheelSpeeds[1]);
        lb.setPower(wheelSpeeds[2]);
        rb.setPower(wheelSpeeds[3]);
    }

    public void drive(double drive, double strafe, double turn, double botHeading) {
        drive(drive, strafe, turn, botHeading, true);
    }

    public void drive(double drive, double strafe, double turn) {
        drive(drive, strafe, turn, 0);
    }

    private static double getTValue(Vector2d point1, Vector2d point2, Vector2d interpolationPoint) {
        if (point1.x == point2.x) {
            return (interpolationPoint.y - point1.y) - (point2.y - point1.y);
        }
        return (interpolationPoint.x - point1.x) - (point2.x - point1.x);
    }

    private static Waypoint intersection(Pose2d botPose, Waypoint[] lineSegment, double radius) {
        ArrayList<Pair<Waypoint, Double>> intersections = new ArrayList<>();

        double m = (lineSegment[0].y - lineSegment[1].y) / (lineSegment[0].x - lineSegment[1].x);
        double b = lineSegment[0].y - m * lineSegment[0].x;

        double h = botPose.x;
        double k = botPose.y;

        double commonTerm = Math.sqrt(Math.pow(m, 2) * (Math.pow(radius, 2) - Math.pow(h, 2)) + (2 * m * h) * (k - b) + 2 * b * k + Math.pow(radius, 2) - Math.pow(b, 2) - Math.pow(k, 2));

        double x1 = (m * (k - b) + h + commonTerm) / (Math.pow(m, 2) + 1);
        double x2 = (m * (k - b) + h - commonTerm) / (Math.pow(m, 2) + 1);

        double y1 = Math.sqrt(Math.pow(radius, 2) - Math.pow((x1 - h), 2)) + k;
        double y2 = Math.sqrt(Math.pow(radius, 2) - Math.pow((x2 - h), 2)) + k;

        Waypoint point0 = new Waypoint(x1, y1, 0, lineSegment[1].targetFollowRotation, lineSegment[1].targetEndRotation);
        Waypoint point1 = new Waypoint(x2, y2, 0, lineSegment[1].targetFollowRotation, lineSegment[1].targetEndRotation);

        intersections.add(new Pair<>(point0, getTValue(lineSegment[0], lineSegment[1], point0)));
        intersections.add(new Pair<>(point1, getTValue(lineSegment[0], lineSegment[1], point1)));

        Pair<Waypoint, Double> bestIntersection;

        bestIntersection = intersections.get(0).second > intersections.get(1).second ? intersections.get(0) : intersections.get(1);

        if (bestIntersection.second > 1 || bestIntersection.second < 0) {
            return null;
        }

        return bestIntersection.first;
    }

    public void setFollowPath(Path path) {
        followPath = path;
        waypointIndex = 0;
        segments = followPath.getLineSegments();
    }

    public void driveToPosition(Waypoint targetPoint, boolean useEndpointHeading) {
        Pose2d botPose = poseSupplier.get();
        Vector2d relativeTargetVector = (new Vector2d(targetPoint.x - botPose.x, targetPoint.y - botPose.y));
        Vector2d movementSpeed = new Vector2d(driveController.calculate(0, relativeTargetVector.magnitude), relativeTargetVector.angle, false).rotate(-botPose.angle);

        double rotSpeed;
        double targetAngle;

        if (useEndpointHeading && targetPoint.targetEndRotation != null) {
            targetAngle = targetPoint.targetEndRotation.getAngleRadians();
        } else if (targetPoint.targetFollowRotation != null) {
            targetAngle = targetPoint.targetFollowRotation.getAngleRadians();
        } else {
            targetAngle = relativeTargetVector.angle;
        }

        rotSpeed = rotController.calculate(0, Rotation2d.getAngleDifferenceRadians(targetAngle, botPose.rotation.getAngleRadians() + Math.PI / 2));
        drive(movementSpeed.y, movementSpeed.x, rotSpeed);
    }

    private Waypoint[] bestFollowSegment() {
        Pose2d botPose = poseSupplier.get();
        Pair<Waypoint[], Double> shortestDistance = new Pair<>(new Waypoint[]{new Waypoint(Vector2d.undefined, 0), new Waypoint(Vector2d.undefined, 0)}, Double.POSITIVE_INFINITY);
        for (int i = waypointIndex; i < segments.length; i++) {
            double distance = new Vector2d(segments[i][1].x - botPose.x, segments[i][1].y - botPose.y).magnitude;
            if (distance < shortestDistance.second) {
                shortestDistance = new Pair<>(segments[i], distance);
                waypointIndex = i;
            }
        }
        return shortestDistance.first;
    }

    public void followPath() {
        Pose2d botPose = poseSupplier.get();
        Waypoint targetPoint;
        boolean endOfPath = false;

        switch (driveState) {
            case DRIVE:
                if (waypointIndex != 0) return;
                followStartTimestamp = timer.milliseconds();
                driveState = DriveState.FOLLOWING;
            case FOLLOWING:
                if (timer.milliseconds() > followStartTimestamp + followPath.timeout) {
                    driveState = DriveState.DRIVE;
                }

                if (waypointIndex < segments.length - 1) {
                    targetPoint = intersection(botPose, segments[waypointIndex], segments[waypointIndex][1].followRadius);
                } else {
                    targetPoint = segments[segments.length - 1][1];
                    endOfPath = true;
                }

                if (targetPoint == null) { // If null is returned, the t value of the target point is greater than 1 or less than 0
                    waypointIndex++;
                    followPath();
                } else if (targetPoint.equals(Vector2d.undefined)) { // If there is no valid intersection, follow the last target point
                    Waypoint[] segment = bestFollowSegment();
                    targetPoint = intersection(botPose, segment, segment[1].followRadius);
                }
                lastWaypoint = targetPoint;
                driveToPosition(targetPoint, endOfPath);
        }
    }

    public boolean finishedFollowing() {
        return false;
    }
}
