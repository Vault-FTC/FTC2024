package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Pair;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.control.PIDController;
import org.firstinspires.ftc.teamcode.utils.PairedEncoder;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

public class Slide extends Subsystem {

    public final DcMotor motor1;
    public final DcMotor motor2;
    public final PairedEncoder encoder;
    private final PIDController controller;
    private final TouchSensor limit;
    private final Placer placer;
    private int targetPosition;
    private double lastSpeed;

    public Slide(DcMotor motor1, DcMotor motor2, PairedEncoder encoder, TouchSensor limit, Placer placer) {
        this.motor1 = motor1;
        this.motor2 = motor2;
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);
        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        this.encoder = encoder;
        encoder.reset();
        targetPosition = 0;
        this.limit = limit;
        this.placer = placer;
        controller = new PIDController(0.0017, 0.0, 0.000003);
        controller.resetIntegralOnSetPointChange = true;
    }

    private void runMotor(double speed) {
        double feedforward = speed > 0 ? Server.getInstance().getLayout("dashboard_0").getDoubleValue("slide feedforward", 0.2) : 0;
        if (speed > 0 && encoder.getPosition() > Constants.Slide.maxExtensionPosition) {
            speed = 0;
        }
        speed += feedforward;
        if (speed > 0 && encoder.getPosition() > Constants.Slide.preparePlacerPosition) {
            placer.placePosition();
        } else if (speed < 0) {
            placer.storagePosition();
        }
        double accelMax = Server.getInstance().getLayout("dashboard_0").getDoubleValue("slide accel", 10.35);
        if (speed > 0) {
            speed = Math.min(lastSpeed + accelMax, speed);
        } else {
            speed = Math.max(lastSpeed - accelMax, speed);
        }
        speed = MathUtils.clamp(speed, -1.0, 1.0);
        speed = Math.max(-Constants.Slide.maxDownSpeed, speed);
        lastSpeed = speed;
        motor1.setPower(speed);
        motor2.setPower(-speed);
        DashboardLayout.setNodeValue("slide speed", speed);
        DashboardLayout.setNodeValue("slide limit", limit.isPressed());
    }

    public void drive(double speed) {
        if (limit.isPressed()) {
            encoder.reset();
            placer.close();
            targetPosition = Math.max(targetPosition, 0);
        }
        runMotor(speed);
    }

    public void stop() {
        drive(0);
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = Math.min(targetPosition, Constants.Slide.maxExtensionPosition);
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public boolean atTargetPosition() {
        return Math.abs(targetPosition - encoder.getPosition()) < Constants.Slide.pidDeadband;
    }

    public void driveToPosition() {
        DashboardLayout.setNodeValue("calculated power", String.valueOf(controller.calculate(encoder.getPosition(), targetPosition)));
        drive(Range.clip(controller.calculate(encoder.getPosition(), targetPosition), -1, 1));
    }

    @Override
    public void periodic() {
        controller.setP(Server.getInstance().getLayout("dashboard_0").getDoubleValue("slide kP", 0.0017));
        controller.setI(Server.getInstance().getLayout("dashboard_0").getDoubleValue("slide kI", 0.0000008));
        controller.setD(Server.getInstance().getLayout("dashboard_0").getDoubleValue("slide kD", 0.000003));
        DashboardLayout.setNodeValue("slide pose", encoder.getPosition());
        DashboardLayout.setNodeValue("slide target", targetPosition);
    }
}
