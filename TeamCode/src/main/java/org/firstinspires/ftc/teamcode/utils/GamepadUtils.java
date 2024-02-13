package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;

public class GamepadUtils {

    Gamepad gamepad1;
    Gamepad gamepad2;

    public GamepadUtils(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }

    public double getTriggerSpeed(double multiplier) {
        double power = 0;
        if (gamepad2.right_trigger > Constants.triggerDeadZone) {
            power = 1;
        } else if (gamepad2.left_trigger > Constants.triggerDeadZone) {
            power = -1;
        }
        return power * Range.clip(multiplier, -1, 1);
    }

    public double getTriggerSpeed() {
        return getTriggerSpeed(1);
    }

}
