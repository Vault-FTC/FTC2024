package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;

public class DroneShooter extends Subsystem {

    public final Servo angleAdjuster;
    public final Servo release;

    public DroneShooter(HardwareMap hardwareMap) {
        angleAdjuster = hardwareMap.get(Servo.class, "angleAdjusterServo");
        release = hardwareMap.get(Servo.class, "releaseServo");
    }
}
