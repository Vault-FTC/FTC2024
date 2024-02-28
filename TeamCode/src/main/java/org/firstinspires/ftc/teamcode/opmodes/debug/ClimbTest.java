package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

//@Disabled
@TeleOp(name = "climb test", group = "debug")
public class ClimbTest extends OpMode {
    DcMotor motor;
    DcMotor motor2;

    DcMotor motor3;

    DcMotor motor4;

    Servo servo;

    TouchSensor limit;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor3 = hardwareMap.get(DcMotor.class, "motor3");
        motor4 = hardwareMap.get(DcMotor.class, "motor4");
        servo = hardwareMap.get(Servo.class, "servo");
        limit = hardwareMap.get(TouchSensor.class, "limit");
    }

    @Override
    public void loop() {
        motor.setPower(gamepad1.right_stick_y);
        motor2.setPower(gamepad1.right_stick_y);
        motor3.setPower(gamepad1.left_stick_y);
        if (gamepad1.right_trigger > 0.1) {
            motor4.setPower(1);
        } else if (gamepad1.left_trigger > 0.1) {
            motor4.setPower(-1);
        } else {
            motor4.setPower(0);
        }
        if (gamepad1.a) {
            servo.setPosition(0.2);
        } else if (gamepad1.b) {
            servo.setPosition(0.6);
        }
        telemetry.addData("limit", limit.isPressed());
    }
}
