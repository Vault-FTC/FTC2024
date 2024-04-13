package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

public class SlideToPosition extends Command {

    private final Slide subsystem;
    private final int position;

    private final Gamepad gamepad;

    public SlideToPosition(Slide slide, int position, Gamepad gamepad) {
        subsystem = slide;
        this.position = position;
        this.gamepad = gamepad;
        addRequirements(subsystem);
    }

    public SlideToPosition(Slide slide, int position) {
        this(slide, position, null);
    }

    @Override
    public void initialize() {
        subsystem.setTargetPosition(position);
    }

    @Override
    public void execute() {
        subsystem.driveToPosition();
    }

    @Override
    public boolean isFinished() {
        return subsystem.atTargetPosition() || (timeSinceInitialized() > 2500 && (gamepad != null && !gamepad.atRest()));
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }
}
