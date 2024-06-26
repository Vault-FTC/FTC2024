package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Constants.Drive.StartPositions;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commands.TimedIntake;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.FutureWaypoint;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;

import java.util.ArrayList;

@Autonomous(name = "Blue Left", group = "blue")
public class BlueLeft extends Auton {
    ArrayList<Path> paths = new ArrayList<>();

    Path leftPath = Path.getBuilder()
            .addWaypoint(StartPositions.blueLeft.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(StartPositions.blueLeft.x - 11.375, 26, Constants.Drive.defaultFollowRadius, null, new Rotation2d()))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(StartPositions.blueLeft.toWaypoint())
            .addWaypoint(StartPositions.blueLeft.x, 33)
            .build();

    Path rightPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(StartPositions.blueLeft.toWaypoint())
            .addWaypoint(StartPositions.blueLeft.x - 6, 24)
            .addWaypoint(new Waypoint(StartPositions.blueLeft.x, 29.5, Constants.Drive.defaultFollowRadius, null, new Rotation2d(-Math.PI / 4)))
            .build();

    private Path getPhenomenomallyPerfectPurplePlacePath() {
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

    private Waypoint getInitialYellowPlaceWaypoint() {
        Waypoint waypoint = getYellowPlaceWaypoint();
        return new Waypoint(waypoint.x + 6.0, waypoint.y, waypoint.followRadius, waypoint.targetFollowRotation, waypoint.targetEndRotation);
    }

    private Waypoint getYellowPlaceWaypoint() {
        double x = 17;
        double y = 33.5;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = 27;
                break;
            case CENTER:
                y = 33.5;
                break;
            case RIGHT:
                y = 39.5;
                break;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, new Rotation2d(-Math.PI / 2), new Rotation2d(-Math.PI / 2), 0.75);
    }

    public BlueLeft() {
        super(Pipeline.Alliance.BLUE, StartPositions.blueLeft);
    }

    @Override
    public void init() {
        super.init();
        paths.add(Path.getBuilder().setTimeout(2000) // Drive away from spike mark path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new FutureWaypoint(() -> {
                    Pose2d botPose = drive.odometry.getPose();
                    return new Waypoint(botPose.x - 8.0, botPose.y - 8.0, 6.0, null, Rotation2d.fromDegrees(-90));
                }))
                .build());
        paths.add(Path.getBuilder().setTimeout(3000).setDefaultMaxVelocity(0.5) // Drive to backdrop path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new FutureWaypoint(this::getInitialYellowPlaceWaypoint))
                .build());
        paths.add(Path.getBuilder().setTimeout(5000) // Park path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new Waypoint(46.0, 30, Constants.Drive.defaultFollowRadius, null, null, 0.4))
                .addWaypoint(new Waypoint(33.0, 25, Constants.Drive.defaultFollowRadius, null, null, 0.5))
                .addWaypoint(new Waypoint(18, 10, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(90)))
                .build());

        autonomousCommand = SequentialCommandGroup.getBuilder()
                .add(new FollowFuturePath(this::getPhenomenomallyPerfectPurplePlacePath, drive)) // Drive to place the purple pixel
                .add(new TimedIntake(intake, -0.7, 1000)) // Run the intake in reverse to spit out the purple pixel
                .add(new InstantCommand(() -> aprilTagCamera.enable()))
                .add(new ParallelCommandGroup( // Drive away from the spike mark and extend the slide
                        new FollowPath(paths.get(0), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(1500),
                                new SlideToPosition(slide, 1200))).setTimeout(5000))
                .add(new WaitCommand(500)) // Wait for a detection
                .add(new FollowPath(paths.get(1), drive)) // Drive to the backdrop
                .add(new InstantCommand(() -> aprilTagCamera.disable())) // Camera is no longer necessary
                .add(new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 1500, 500))
                .add(new InstantCommand(() -> placer.open())) // Place the pixel
                .add(new WaitCommand(1500))
                .add(new ParallelCommandGroup( // Close the placer, stow the slide, and park
                        new FollowPath(paths.get(2), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(1250),
                                new ParallelCommandGroup(new InstantCommand(() -> placer.close()), new SlideToPosition(slide, 0)))
                ))
                .build();
    }

    @Override
    public void loop() {
        super.loop();
    }
}
