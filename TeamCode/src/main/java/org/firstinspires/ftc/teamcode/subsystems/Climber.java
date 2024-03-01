package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;

public class Climber extends Subsystem {

    public final DcMotor motor;

    public Climber(DcMotor motor) {
        this.motor = motor;
    }

    public void run(double speed) {
        motor.setPower(speed);
    }
}
