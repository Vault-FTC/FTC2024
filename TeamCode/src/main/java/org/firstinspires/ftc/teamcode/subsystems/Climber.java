package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;

public class Climber extends Subsystem {

    public final DcMotor motor;
    public final Servo servo;

    public Climber(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "climbMotor");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        servo = hardwareMap.get(Servo.class, "climbServo");
    }

    public void deliverHook() {
        servo.setPosition(0.1);
    }

    public void hookDown() {
        servo.setPosition(0.5);
    }

    public void winch(double speed) {
        motor.setPower(speed);
    }
}
