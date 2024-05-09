package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

@Autonomous(name = "Red Left Only Purple")
public class RedLeftOnlyPurple extends Auton {

    public RedLeftOnlyPurple() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redLeft);
    }

    Path leftPath = Path.getBuilder()
            .addWaypoint(Constants.Drive.StartPositions.redLeft.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redLeft.x - 12, 30, DriveConstants.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.8))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redLeft.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redLeft.x, 35.5, DriveConstants.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.7))
            .build();

    Path rightPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redLeft.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redLeft.x - 6, 24, DriveConstants.defaultFollowRadius, new Rotation2d(), null))
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redLeft.x + 6, 32, DriveConstants.defaultFollowRadius, null, Rotation2d.fromDegrees(-45), 0.8))
            .build();

    private Path getPhenomenallyPerfectPurplePlacePath() {
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                return leftPath;
            case CENTER:
                return centerPath;
            case RIGHT:
                return rightPath;
        }
        return centerPath;
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = new SequentialCommandGroup(
                new FollowFuturePath(this::getPhenomenallyPerfectPurplePlacePath, drive), // Drive to the spike mark
                new InstantCommand(purplePixelPlacer::place), // Place the pixel
                new WaitCommand(DashboardLayout.loadDouble("wait_time_RL", 1000)), // Wait for the pixel to drop
                new ParallelCommandGroup(
                        new FollowPath(Path.loadPath("backup_RL"), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(1000),
                                new InstantCommand(() -> purplePixelPlacer.retract())
                        )
                ));
    }

}
