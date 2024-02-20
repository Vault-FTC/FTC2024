package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.tele.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

public class DriveToBackboard extends Command {

    private final Drive drive;
    private final Gamepad gamepad;

    public DriveToBackboard(Drive drive, Gamepad gamepad) {
        this.drive = drive;
        this.gamepad = gamepad;
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        Pose2d botPose = drive.odometry.getPose();
        drive.base.setFollowPath(Path.getBuilder().setDefaultRadius(8).addWaypoint(botPose.x, botPose.y).addWaypoint(Robot.backdropPose.toWaypoint()).build());
    }

    @Override
    public void execute() {
        drive.base.followPath();
        if (timeSinceInitialized() > 250 && !gamepad.atRest()) {
            cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return drive.base.finishedFollowing();
    }
}
