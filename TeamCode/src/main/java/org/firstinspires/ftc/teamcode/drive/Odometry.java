package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.DriveConstants.OdometryConstants;
import org.firstinspires.ftc.teamcode.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.rustboard.RustboardLayout;
import org.firstinspires.ftc.teamcode.utils.Encoder;
import org.firstinspires.ftc.teamcode.utils.PairedEncoder;

public class Odometry {
    public final Encoder parallel0; // right encoder
    public final Encoder parallel1; // left encoder
    public final Encoder perpendicular;

    private int lastPar0 = 0;
    private int lastPar1 = 0;
    private int lastPerpendicular = 0;

    private Pose2d pose;

    public Odometry(HardwareMap hardwareMap) {
        pose = new Pose2d();
        parallel0 = new PairedEncoder(hardwareMap.get(DcMotor.class, "rb"), true);
        parallel1 = new PairedEncoder(hardwareMap.get(DcMotor.class, "lb"), true);
        perpendicular = new PairedEncoder(hardwareMap.get(DcMotor.class, "climbMotor"));
        resetEncoders();
    }

    public void setPosition(Pose2d pose) {
        this.pose = pose;
    }

    private Pose2d delta() {
        int currentPar0 = parallel0.getPosition();
        int currentPar1 = parallel1.getPosition();
        int currentPerpendicular = perpendicular.getPosition();

        double par0Delta = currentPar0 - lastPar0;
        double par1Delta = currentPar1 - lastPar1;
        double perpendicularDelta = currentPerpendicular - lastPerpendicular;

        double deltaHeading = OdometryConstants.inPerTick * (par0Delta - par1Delta) / OdometryConstants.trackWidth;

        double deltaXDrive;
        double deltaYDrive;

        double deltaXStrafe;
        double deltaYStrafe;

        if (deltaHeading == 0.0) {
            deltaXDrive = 0;
            deltaYDrive = OdometryConstants.inPerTick * par0Delta;

            deltaXStrafe = OdometryConstants.inPerTick * perpendicularDelta;
            deltaYStrafe = 0;
        } else {
            double par0Radius = OdometryConstants.inPerTick * par0Delta / deltaHeading;
            double par1Radius = OdometryConstants.inPerTick * par1Delta / deltaHeading;

            double driveRadius = (par0Radius + par1Radius) / 2;

            deltaXDrive = -driveRadius * (1 - Math.cos(deltaHeading));
            deltaYDrive = driveRadius * Math.sin(deltaHeading);

            double strafeRadius = OdometryConstants.inPerTick * perpendicularDelta / deltaHeading - OdometryConstants.horizontalDist;

            deltaXStrafe = strafeRadius * Math.sin(deltaHeading);
            deltaYStrafe = strafeRadius * (1 - Math.cos(deltaHeading));
        }

        lastPar0 = currentPar0;
        lastPar1 = currentPar1;
        lastPerpendicular = currentPerpendicular;

        return new Pose2d(deltaXDrive + deltaXStrafe, deltaYDrive + deltaYStrafe, new Rotation2d(deltaHeading));
    }

    public Pose2d update() {
        RustboardLayout.setNodeValue("encoder0", parallel0.getPosition());
        RustboardLayout.setNodeValue("encoder1", parallel1.getPosition());
        RustboardLayout.setNodeValue("encoder2", perpendicular.getPosition());
        Pose2d delta = delta();
        pose = pose.add(new Pose2d(delta.rotate(pose.rotation.getAngleRadians()), new Rotation2d(delta.rotation.getAngleRadians())));
        return pose;
    }

    public void resetEncoders() {
        parallel0.reset();
        parallel1.reset();
        perpendicular.reset();
    }

    public Pose2d getPose() {
        return pose;
    }

}
