package org.firstinspires.ftc.teamcode.drive;

import android.util.Pair;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.utils.PIDController;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

import java.util.function.Supplier;

public class MecanumBase {
    public final DcMotor lf;
    public final DcMotor rf;
    public final DcMotor lb;
    public final DcMotor rb;

    private int waypointIndex = 0;

    private Path followPath;
    private final ElapsedTime timer = new ElapsedTime();

    private Pose2d lastPose = new Pose2d();
    private double lastTimestamp = 0;
    private double followStartTimestamp;
    private Waypoint[][] segments;

    private double lastTargetAngle = 0;

    public enum DriveState {
        IDLE,
        FOLLOWING,

    }

    public DriveState driveState = DriveState.IDLE;

    private final Supplier<Pose2d> poseSupplier;
    public PIDController driveController = new PIDController(0.25, 0.0, 2);

    public PIDController rotController = new PIDController(2.0, 0.0002, 0.6);

    public MecanumBase(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack, Supplier<Pose2d> poseSupplier) {
        lf = leftFront;
        rf = rightFront;
        lb = leftBack;
        rb = rightBack;
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.poseSupplier = poseSupplier;
        timer.startTime();
    }

    public void drive(double drive, double strafe, double turn, double botHeading, boolean squareInputs) {
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
            return (interpolationPoint.y - point1.y) / (point2.y - point1.y);
        }
        return (interpolationPoint.x - point1.x) / (point2.x - point1.x);
    }


    public void setFollowPath(Path path) {
        followPath = path;
        waypointIndex = 0;
        segments = followPath.getLineSegments();
    }

    public void driveToPosition(Waypoint targetPoint, boolean useEndpointHeading) {
        Pose2d botPose = poseSupplier.get();
        Vector2d relativeTargetVector = (new Vector2d(targetPoint.x - botPose.x, targetPoint.y - botPose.y));
        Vector2d movementSpeed = (new Vector2d(driveController.calculate(0, relativeTargetVector.magnitude), relativeTargetVector.angle, false)).rotate(-botPose.rotation.getAngleRadians());

        double rotSpeed;
        double targetAngle;

        if (useEndpointHeading && targetPoint.targetEndRotation != null && botPose.distanceTo(targetPoint) < Constants.Drive.trackEndpointHeadingMaxDistance) {
            targetAngle = targetPoint.targetEndRotation.getAngleRadians();
        } else if (targetPoint.targetFollowRotation != null) {
            targetAngle = targetPoint.targetFollowRotation.getAngleRadians();
        } else if (relativeTargetVector.magnitude > Constants.Drive.calculateTargetHeadingMinDistance) {
            targetAngle = relativeTargetVector.angle - Math.PI / 2;
        } else {
            targetAngle = lastTargetAngle;
        }
        lastTargetAngle = targetAngle;

        double rotError = Rotation2d.getError(targetAngle, botPose.rotation.getAngleRadians());
        double magnitude = movementSpeed.magnitude / (1.5 * Math.pow(Math.abs(rotError), 2) + 1); // originally 0.9 * rotError ^ 2
        magnitude = Range.clip(magnitude, -targetPoint.maxVelocity, targetPoint.maxVelocity);
        movementSpeed = new Vector2d(magnitude, movementSpeed.angle, false);
        rotSpeed = rotController.calculate(0, rotError);

        drive(movementSpeed.y, movementSpeed.x, rotSpeed);
    }

    private static Waypoint intersection(Pose2d botPose, Waypoint[] lineSegment, double radius) {
        double x1;
        double y1;

        double x2;
        double y2;

        double m = (lineSegment[0].y - lineSegment[1].y) / (lineSegment[0].x - lineSegment[1].x);
        double b = lineSegment[0].y - m * lineSegment[0].x;

        double h = botPose.x;
        double k = botPose.y;

        double commonTerm;

        if (Double.isFinite(m)) {
            commonTerm = Math.sqrt(Math.pow(m, 2) * (Math.pow(radius, 2) - Math.pow(h, 2)) + (2 * m * h) * (k - b) + 2 * b * k + Math.pow(radius, 2) - Math.pow(b, 2) - Math.pow(k, 2));

            x1 = (m * (k - b) + h + commonTerm) / (Math.pow(m, 2) + 1);
            x2 = (m * (k - b) + h - commonTerm) / (Math.pow(m, 2) + 1);

            y1 = m * x1 + b;
            y2 = m * x2 + b;
        } else { // Vertical line
            x1 = lineSegment[0].x;
            commonTerm = Math.sqrt(Math.pow(radius, 2) - Math.pow((x1 - h), 2));
            y1 = botPose.y + commonTerm;
            x2 = x1;
            y2 = botPose.y - commonTerm;
        }

        Waypoint point0 = new Waypoint(x1, y1, 0, lineSegment[1].targetFollowRotation, lineSegment[1].targetEndRotation);
        Waypoint point1 = new Waypoint(x2, y2, 0, lineSegment[1].targetFollowRotation, lineSegment[1].targetEndRotation);

        Pair<Waypoint, Double> intersection0 = new Pair<>(point0, getTValue(lineSegment[0], lineSegment[1], point0));
        Pair<Waypoint, Double> intersection1 = new Pair<>(point1, getTValue(lineSegment[0], lineSegment[1], point1));

        Pair<Waypoint, Double> bestIntersection;

        bestIntersection = intersection0.second > intersection1.second ? intersection0 : intersection1;

        if (bestIntersection.second > 1 || bestIntersection.second < 0) {
            return null;
        }

        return bestIntersection.first;
    }

    private Waypoint[] closestSegment() {
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
            case IDLE:
                if (waypointIndex != 0) return;
                followStartTimestamp = timer.milliseconds();
                driveState = DriveState.FOLLOWING;
            case FOLLOWING:
                if (timer.milliseconds() > followStartTimestamp + followPath.timeout) {
                    driveState = DriveState.IDLE;
                }

                targetPoint = intersection(botPose, segments[waypointIndex], segments[waypointIndex][1].followRadius);

                if (targetPoint == null) { // If null is returned, the t value of the target point is greater than 1 or less than 0
                    if (waypointIndex == segments.length - 1) {
                        targetPoint = segments[segments.length - 1][1];
                        endOfPath = true;
                    } else {
                        waypointIndex++;
                        followPath();
                        return;
                    }
                } else if (targetPoint.equals(Vector2d.undefined)) { // If there is no valid intersection, follow the endpoint of the current segment
                    targetPoint = segments[waypointIndex][1];
                }

                driveToPosition(targetPoint, endOfPath);
        }
    }

    public boolean finishedFollowing() {
        if (timer.milliseconds() > followStartTimestamp + followPath.timeout) {
            return true;
        }

        boolean atEndpoint;
        boolean atTargetHeading;

        Pose2d botPose = poseSupplier.get();
        double currentTimestamp = timer.milliseconds();
        double speed = botPose.distanceTo(lastPose) / (currentTimestamp - lastTimestamp) * 1000;

        lastTimestamp = currentTimestamp;

        double distanceToEndpoint = new Vector2d(segments[segments.length - 1][1].x - botPose.x, segments[segments.length - 1][1].y - botPose.y).magnitude;

        atEndpoint = speed < 2.0 && distanceToEndpoint < 2.0 && waypointIndex == segments.length - 1;
        if (segments[segments.length - 1][1].targetEndRotation == null) {
            atTargetHeading = true;
        } else {
            atTargetHeading = Math.abs(Rotation2d.getError(botPose.rotation.getAngleRadians(), segments[segments.length - 1][1].targetEndRotation.getAngleRadians())) < Math.toRadians(5);
        }

        DashboardLayout.setNodeValue("follow", speed);

        lastPose = botPose;
        return atEndpoint && atTargetHeading;
    }

}
