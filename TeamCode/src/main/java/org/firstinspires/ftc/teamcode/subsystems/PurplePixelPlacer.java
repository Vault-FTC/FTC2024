package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Rustboard;

public class PurplePixelPlacer extends Subsystem {

    private final Servo servo;

    public PurplePixelPlacer(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "purplePixelPlacer");
    }

    public void place() {
        servo.setPosition(Rustboard.getLayout("dashboard_0").getDoubleValue("purple place", 0.95));
    }

    public void retract() {
        servo.setPosition(Rustboard.getLayout("dashboard_0").getDoubleValue("purple retract", 0.6));
    }
}
