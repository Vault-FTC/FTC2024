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
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;

import java.util.ArrayList;

@Autonomous(name = "Blue Right", group = "blue")
public class BlueRight extends Auton {
    ArrayList<Path> paths = new ArrayList<>();

    Path leftPath = Path.getBuilder()
            .addWaypoint(StartPositions.blueRight.toWaypoint())
            .addWaypoint(StartPositions.blueRight.x + 6, 23)
            .addWaypoint(new Waypoint(StartPositions.blueRight.x - 1, 27, Constants.Drive.defaultFollowRadius, null, new Rotation2d(-Math.PI / 4)))
            .build();
    Path centerPath = Path.getBuilder()
            .addWaypoint(StartPositions.blueRight.toWaypoint())
            .addWaypoint(StartPositions.blueRight.x, 33.5)
            .build();

    Path rightPath = Path.getBuilder()
            .addWaypoint(StartPositions.blueRight.toWaypoint())
            .addWaypoint(new Waypoint(StartPositions.blueRight.x + 11.375, 24, Constants.Drive.defaultFollowRadius, null, new Rotation2d()))
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
        double y = 34.5;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = 28.25;
                break;
            case CENTER:
                y = 34.5;
                break;
            case RIGHT:
                y = 39.75;
                break;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, new Rotation2d(-Math.PI / 2), new Rotation2d(-Math.PI / 2));
    }

    public BlueRight() {
        super(Pipeline.Alliance.BLUE, StartPositions.blueRight);
    }

    @Override
    public void init() {
        super.init();
        paths.add(Path.getBuilder().setTimeout(6000) // Drive away from spike mark and through rigging path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new Waypoint(115, 40, 8.0, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90)))
                .addWaypoint(new Waypoint(55, 40, 8.0, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90)))
                .build());
        paths.add(Path.getBuilder().setTimeout(5000).setDefaultMaxVelocity(0.5) // Drive to backdrop path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new FutureWaypoint(this::getInitialYellowPlaceWaypoint))
                .build());
        paths.add(Path.getBuilder().setTimeout(5000) // Park path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new Waypoint(23.0, 30, Constants.Drive.defaultFollowRadius, null, null, 0.5))
                .build());

        autonomousCommand = SequentialCommandGroup.getBuilder()
                .add(new FollowFuturePath(this::getPhenomenomallyPerfectPurplePlacePath, drive)) // Drive to place the purple pixel
                .add(new TimedIntake(intake, -0.7, 1000)) // Run the intake in reverse to spit out the purple pixel
                .add(new FollowPath(paths.get(0), drive))  // Drive away from the spike mark and through the rigging
                .add(new InstantCommand(() -> aprilTagCamera.enable()))
                .add(new WaitCommand(500))
                .add(new ParallelCommandGroup( // Drive to the backdrop and extend the slide
                        new FollowPath(paths.get(1), drive),
                        new SlideToPosition(slide, 1200)).setTimeout(5000))
                .add(new InstantCommand(() -> aprilTagCamera.disable())) // Camera is no longer necessary
                .add(new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 2000, 500))
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
