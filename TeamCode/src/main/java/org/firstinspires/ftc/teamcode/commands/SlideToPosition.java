package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

public class SlideToPosition extends Command {

    private final Slide subsystem;
    private final Gamepad gamepad;
    private final int position;

    public SlideToPosition(Slide slide, Gamepad gamepad, int position) {
        subsystem = slide;
        this.gamepad = gamepad;
        this.position = position;
    }

    @Override
    public void initialize() {
        subsystem.setTargetPosition(position);
    }

    @Override
    public void execute() {
        if (Constants.ControlSettings.slideManualControl && Math.abs(gamepad.right_stick_y) > Constants.joystickDeadZone) {
            cancel();
        }
        subsystem.driveToPosition();
    }

    @Override
    public boolean isFinished() {
        return subsystem.atTargetPosition();
    }
}
