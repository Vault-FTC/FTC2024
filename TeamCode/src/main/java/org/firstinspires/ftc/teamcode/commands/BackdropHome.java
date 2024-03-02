package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.FutureWaypoint;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;

public class BackdropHome extends Command {

    private SequentialCommandGroup sequence;

    private final Pose2d backdropPose;

    public BackdropHome(Robot robot, Pose2d backdropPose) {
        this.backdropPose = backdropPose;
        sequence = new SequentialCommandGroup(
                new WaitCommand(500),
                new FollowPath(Path.getBuilder()
                        .addWaypoint(new FutureWaypoint(() -> robot.drive.odometry.getPose().toWaypoint()))
                        .addWaypoint(backdropPose.toWaypoint())
                        .build(), robot.drive));
    }

    @Override
    public void initialize() {


    }

    @Override
    public void execute() {

    }
}
