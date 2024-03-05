package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

public class Odometry {

    public static final class Params {
        public static final double trackWidth = 13.5433071;
        public static final double horizontalDist = 7.80; //7.35
        public static double inPerTick = 0.002968431495;
    }

    public final Encoder parallel0; // right encoder
    public final Encoder parallel1; // left encoder
    public final Encoder perpendicular;

    private int lastPar0 = 0;
    private int lastPar1 = 0;
    private int lastPerpendicular = 0;

    private Pose2d pose;

    public Odometry(HardwareMap hardwareMap) {
        pose = new Pose2d();
        parallel0 = new PairedEncoder(hardwareMap.get(DcMotor.class, "lf"), false); //lf
        parallel1 = new PairedEncoder(hardwareMap.get(DcMotor.class, "rf"), false); //lb
        perpendicular = new PairedEncoder(hardwareMap.get(DcMotor.class, "lb"), false); //rf
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

        double deltaHeading = Params.inPerTick * (par0Delta - par1Delta) / Params.trackWidth;

        double deltaXDrive;
        double deltaYDrive;

        double deltaXStrafe;
        double deltaYStrafe;

        if (deltaHeading == 0.0) {
            deltaXDrive = 0;
            deltaYDrive = Params.inPerTick * par0Delta;

            deltaXStrafe = Params.inPerTick * perpendicularDelta;
            deltaYStrafe = 0;
        } else {
            double par0Radius = Params.inPerTick * par0Delta / deltaHeading;
            double par1Radius = Params.inPerTick * par1Delta / deltaHeading;

            double driveRadius = (par0Radius + par1Radius) / 2;

            deltaXDrive = -driveRadius * (1 - Math.cos(deltaHeading));
            deltaYDrive = driveRadius * Math.sin(deltaHeading);

            double strafeRadius = Params.inPerTick * perpendicularDelta / deltaHeading - Params.horizontalDist;

            deltaXStrafe = strafeRadius * Math.sin(deltaHeading);
            deltaYStrafe = strafeRadius * (1 - Math.cos(deltaHeading));
        }

        lastPar0 = currentPar0;
        lastPar1 = currentPar1;
        lastPerpendicular = currentPerpendicular;

        return new Pose2d(deltaXDrive + deltaXStrafe, deltaYDrive + deltaYStrafe, new Rotation2d(deltaHeading));
    }

    public Pose2d update() {
        DashboardLayout.setNodeValue("encoder0", parallel0.getPosition());
        DashboardLayout.setNodeValue("encoder1", parallel1.getPosition());
        DashboardLayout.setNodeValue("encoder2", perpendicular.getPosition());
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
