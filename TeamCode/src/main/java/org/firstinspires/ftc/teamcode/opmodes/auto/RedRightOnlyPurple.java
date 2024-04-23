package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

@Autonomous(name = "Red Right Only Purple")
public class RedRightOnlyPurple extends Auton {

    public RedRightOnlyPurple() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redRight);
    }

    Path leftPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 6, 24, Constants.Drive.defaultFollowRadius, new Rotation2d(), null))
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x + 6, 32, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(45), 0.8))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x, 35.5, Constants.Drive.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.7))
            .build();

    Path rightPath = Path.getBuilder()
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 12, 30, Constants.Drive.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.8))
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
                new WaitCommand(DashboardLayout.loadDouble("wait_time_RR", 1000)), // Wait for the pixel to drop
                new ParallelCommandGroup(
                        new FollowPath(Path.loadPath("place_and_park_RR"), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(1000),
                                new InstantCommand(() -> purplePixelPlacer.retract())
                        )
                ));
    }

}
