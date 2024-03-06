package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(name = "two motor test", group = "debug")
public class SlideMotorTest extends OpMode {
    DcMotor motor;
    DcMotor motor2;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
    }

    @Override
    public void loop() {
        motor.setPower(gamepad1.right_stick_y);
        motor2.setPower(gamepad1.right_stick_y);
    }
}
