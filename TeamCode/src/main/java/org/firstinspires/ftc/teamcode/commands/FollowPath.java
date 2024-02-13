package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

public class FollowPath extends Command {

    private final Path path;
    private final Drive subsystem;

    public FollowPath(Path path, Drive drive) {
        this.path = path;
        subsystem = drive;
    }

    @Override
    public void initialize() {
        subsystem.base.setFollowPath(path);
    }

    @Override
    public void execute() {
        subsystem.base.followPath();
    }

    @Override
    public boolean isFinished() {
        return subsystem.base.finishedFollowing();
    }
}
