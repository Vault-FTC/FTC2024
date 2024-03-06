package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;

public class Placer extends Subsystem {
    private final Servo servo;

    public ColorRangeSensor distanceSensor;
    public final TouchSensor touchSensor;

    public Placer(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "placerServo");
        servo.setDirection(Servo.Direction.FORWARD);
        servo.close();
        distanceSensor = hardwareMap.get(ColorRangeSensor.class, "distanceSensor");
        touchSensor = hardwareMap.get(TouchSensor.class, "placerTouch");
    }

    public void open() {
        servo.setPosition(Constants.Placer.placePosition);
    }

    public void close() {
        servo.setPosition(Constants.Placer.closePosition);
    }

    public double getDistance() {
        return distanceSensor.getDistance(DistanceUnit.INCH);
    }
}
