package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Trigger;

import java.util.function.DoubleSupplier;

public class GamepadHelper {
    public Trigger a;

    public Trigger b;

    public Trigger x;

    public Trigger y;

    public final Trigger leftBumper;

    public final Trigger rightBumper;
    public final Trigger leftTrigger;
    public final Trigger rightTrigger;
    public final Trigger dpadLeft;
    public final Trigger dpadRight;
    public final Trigger dpadUp;
    public final Trigger dpadDown;

    public final Trigger circle;

    public final Trigger cross;

    public final Trigger options;
    public final DoubleSupplier leftStickX;
    public final DoubleSupplier leftStickY;
    public final DoubleSupplier rightStickX;
    public final DoubleSupplier rightStickY;

    public GamepadHelper(Gamepad gamepad) {
        a = new Trigger(() -> gamepad.a);
        b = new Trigger(() -> gamepad.b);
        x = new Trigger(() -> gamepad.x);
        y = new Trigger(() -> gamepad.y);
        leftBumper = new Trigger(() -> gamepad.left_bumper);
        rightBumper = new Trigger(() -> gamepad.right_bumper);
        leftTrigger = new Trigger(() -> gamepad.left_trigger > Constants.triggerDeadZone);
        rightTrigger = new Trigger(() -> gamepad.right_trigger > Constants.triggerDeadZone);
        dpadLeft = new Trigger(() -> gamepad.dpad_left);
        dpadRight = new Trigger(() -> gamepad.dpad_right);
        dpadUp = new Trigger(() -> gamepad.dpad_up);
        dpadDown = new Trigger(() -> gamepad.dpad_down);
        circle = new Trigger(() -> gamepad.circle);
        cross = new Trigger(() -> gamepad.cross);
        options = new Trigger(() -> gamepad.options);
        leftStickX = () -> Math.abs(gamepad.left_stick_x > Constants.joystickDeadZone ? gamepad.left_stick_x : 0);
        leftStickY = () -> Math.abs(gamepad.left_stick_y > Constants.joystickDeadZone ? gamepad.left_stick_y : 0);
        rightStickX = () -> Math.abs(gamepad.right_stick_x > Constants.joystickDeadZone ? gamepad.right_stick_x : 0);
        rightStickY = () -> Math.abs(gamepad.right_stick_y > Constants.joystickDeadZone ? gamepad.right_stick_y : 0);

    }

}
