package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

public class FollowPath extends Command {

    private final Path path;
    private final Drive subsystem;

    Gamepad gamepad;

    public FollowPath(Path path, Drive drive, Gamepad gamepad) {
        this.path = path;
        subsystem = drive;
        this.gamepad = gamepad;
    }

    public FollowPath(Path path, Drive drive) {
        this(path, drive, null);
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        subsystem.base.setFollowPath(path);
    }

    @Override
    public void execute() {
        subsystem.base.followPath();
        if (gamepad != null && timeSinceInitialized() > 250 && !gamepad.atRest()) {
            cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return subsystem.base.finishedFollowing();
    }

    @Override
    public void end(boolean interrupted) {
        if (!interrupted) subsystem.drive(0, 0, 0);
    }
}
