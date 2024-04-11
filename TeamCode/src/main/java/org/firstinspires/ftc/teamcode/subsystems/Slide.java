package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.control.PIDController;
import org.firstinspires.ftc.teamcode.utils.PairedEncoder;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

public class Slide extends Subsystem {

    public final DcMotor motor1;
    public final DcMotor motor2;
    public final PairedEncoder encoder;
    private final PIDController controller;
    private final TouchSensor limit;
    private final Placer placer;
    private int targetPosition;
    private final int polarity;

    public Slide(DcMotor motor1, DcMotor motor2, TouchSensor limit, Placer placer, boolean reversed) {
        this.motor1 = motor1;
        this.motor2 = motor2;
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);
        encoder = new PairedEncoder(motor2, true);
        encoder.reset();
        this.limit = limit;
        this.placer = placer;
        controller = new PIDController(0.015, 0.0, 0.1);
        polarity = reversed ? -1 : 1;
    }

    public Slide(DcMotor motor1, DcMotor motor2, TouchSensor limit, Placer placer) {
        this(motor1, motor2, limit, placer, false);
    }

    private void runMotor(double speed) {
        speed *= polarity;
        if (speed > 0 && encoder.getPosition() > Constants.Slide.maxExtensionPosition) {
            speed = 0;
        }
        if (speed > 0 && encoder.getPosition() > Constants.Slide.preparePlacerPosition) {
            //placer.placePosition();
        } else if (speed < 0) {
            //placer.storagePosition();
        }
        motor1.setPower(speed);
        motor2.setPower(-speed);
        DashboardLayout.setNodeValue("slide speed", speed);
        DashboardLayout.setNodeValue("slide pose", encoder.getPosition());
        DashboardLayout.setNodeValue("slide limit", limit.isPressed());
    }


    public void drive(double speed) {
        if (limit.isPressed()) {
            encoder.reset();
        }
        runMotor(speed);
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean atTargetPosition() {
        return Math.abs(targetPosition - encoder.getPosition()) < Constants.Slide.pidDeadband;
    }

    public void driveToPosition() {
        drive(-Range.clip(controller.calculate(encoder.getPosition(), targetPosition), -1, 1));
    }
}
