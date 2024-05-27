package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.constants.DriveConstants;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.org.rustlib.control.PIDController;
import org.firstinspires.ftc.teamcode.org.rustlib.drive.MecanumBase;
import org.firstinspires.ftc.teamcode.org.rustlib.drive.Odometry;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.org.rustlib.hardware.PairedEncoder;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Rustboard;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.RustboardLayout;

public class Drive extends Subsystem {
    public final MecanumBase base;
    public final Odometry odometry;
    private Rotation2d fieldCentricOffset = new Rotation2d();

    public Drive(HardwareMap hardwareMap) {
        odometry = Odometry.getBuilder()
                .defineLeftEncoder(new PairedEncoder(hardwareMap.get(DcMotor.class, "rb"), true))
                .defineRightEncoder(new PairedEncoder(hardwareMap.get(DcMotor.class, "lb"), true))
                .defineBackEncoder(new PairedEncoder(hardwareMap.get(DcMotor.class, "climbMotor")))
                .setTrackWidth(DriveConstants.Odometry.trackWidth)
                .setVerticalDistance(DriveConstants.Odometry.verticalDistance)
                .setInPerTick(DriveConstants.Odometry.inPerTick)
                .build();
        base = MecanumBase.getBuilder()
                .defineLeftFront(hardwareMap.get(DcMotor.class, "lf"), true)
                .defineRightFront(hardwareMap.get(DcMotor.class, "rf"))
                .defineLeftBack(hardwareMap.get(DcMotor.class, "lb"), true)
                .defineRightBack(hardwareMap.get(DcMotor.class, "rb"))
                .setPoseSupplier(odometry::getPose)
                .setMaxEndpointErr(DriveConstants.maxEndpointErr)
                .setUseEndpointHeadingDistance(DriveConstants.trackEndpointHeadingMaxDistance)
                .setTargetHeadingCalculationDistance(DriveConstants.calculateTargetHeadingMinDistance)
                .setMaxFinalVelocity(DriveConstants.maxFinalVelocityInPerSec)
                .setDriveGains(DriveConstants.driveGains)
                .setRotGains(DriveConstants.rotGains)
                .build();
    }

    public void drive(double drive, double strafe, double turn, double heading) {
        base.drive(drive * multiplier, strafe * multiplier, turn * multiplier, heading - fieldCentricOffset.getAngleRadians(), false);
        RustboardLayout.setNodeValue("input", "drive: " + drive + " strafe: " + strafe + " turn: " + turn);
    }

    public void drive(double drive, double strafe, double turn) {
        drive(drive, strafe, turn, odometry.getPose().rotation.getAngleRadians());
    }

    public void setFieldCentricOffset(Rotation2d fieldCentricOffset) {
        this.fieldCentricOffset = fieldCentricOffset;
    }

    public enum Mode {
        FAST(1.0),
        SLOW(0.25);

        final double multiplier;

        Mode(double multiplier) {
            this.multiplier = multiplier;
        }
    }

    private double multiplier = Mode.FAST.multiplier;

    public void enableFastMode() {
        multiplier = Mode.FAST.multiplier;
    }

    public void enableSlowMode() {
        multiplier = Mode.SLOW.multiplier;
    }

    @Override
    public void periodic() {
        RustboardLayout.setNodeValue("pose", odometry.getPose().toString());
        RustboardLayout layout = Rustboard.getLayout("dashboard_0");
        base.driveController.setGains(new PIDController.PIDGains(
                layout.getDoubleValue("drive kP", 0.1),
                layout.getDoubleValue("drive kI", 0.0),
                layout.getDoubleValue("drive kD", 0.0001)));
        base.rotController.setGains(new PIDController.PIDGains(
                layout.getDoubleValue("rot kP", 1.0),
                layout.getDoubleValue("rot kI", 0.0),
                layout.getDoubleValue("rot kD", 0.0)));
    }

}
