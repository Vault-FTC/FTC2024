package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.drive.Encoder;
import org.firstinspires.ftc.teamcode.drive.PairedEncoder;
import org.firstinspires.ftc.teamcode.utils.PIDController;

public class Slide extends Subsystem {

    public final DcMotor motor;
    private final Encoder encoder;
    private final PIDController controller;
    private final TouchSensor limit;
    private int targetPosition;

    public Slide(DcMotor motor, TouchSensor limit) {
        this.motor = motor;
        encoder = new PairedEncoder(motor);
        encoder.reset();
        this.limit = limit;
        controller = new PIDController(0.001, 0, 0);
    }

    private void runMotor(double speed) {
        if (speed > 0 && encoder.getPosition() > Constants.Slide.maxExtensionPosition) {
            speed = 0;
        } else if (speed < 0 && encoder.getPosition() < 0) {
            speed = -0.3;
        }
        motor.setPower(speed);
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
        drive(Range.clip(controller.calculate(encoder.getPosition(), targetPosition), -1, 1));
    }
}
