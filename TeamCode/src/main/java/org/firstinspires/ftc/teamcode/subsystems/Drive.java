package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.control.PIDController;
import org.firstinspires.ftc.teamcode.drive.MecanumBase;
import org.firstinspires.ftc.teamcode.drive.Odometry;
import org.firstinspires.ftc.teamcode.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.rustboard.RustboardLayout;
import org.firstinspires.ftc.teamcode.rustboard.Server;

public class Drive extends Subsystem {
    public final MecanumBase base;
    public final Odometry odometry;

    private Rotation2d fieldCentricOffset = new Rotation2d();

    public Drive(HardwareMap hardwareMap) {
        odometry = new Odometry(hardwareMap);
        base = new MecanumBase(
                hardwareMap.get(DcMotor.class, "lf"),
                hardwareMap.get(DcMotor.class, "rf"),
                hardwareMap.get(DcMotor.class, "lb"),
                hardwareMap.get(DcMotor.class, "rb"), odometry::getPose);
        base.lf.setDirection(DcMotorSimple.Direction.REVERSE);
        base.lb.setDirection(DcMotorSimple.Direction.REVERSE);
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
        odometry.update();
        RustboardLayout.setNodeValue("pose", odometry.getPose().toString());
        RustboardLayout layout = Server.getLayout("dashboard_0");
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
