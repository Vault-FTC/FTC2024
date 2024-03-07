package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

public class Climber extends Subsystem {

    public final DcMotor motor;
    public final Servo servo;

    public Climber(HardwareMap hardwareMap) {
        this.motor = hardwareMap.get(DcMotor.class, "climbMotor");
        this.servo = hardwareMap.get(Servo.class, "climbServo");
    }

    public void deliverHook() {
        double servoPosition;
        try {
            servoPosition = Double.parseDouble(WebdashboardServer.getInstance().getFirstConnectedLayout().getInputValue("climb"));
        } catch (Exception e) {
            servoPosition = 0.4;
        }
        servo.setPosition(servoPosition);
    }

    public void winch(double speed) {
        motor.setPower(speed);
    }
}
