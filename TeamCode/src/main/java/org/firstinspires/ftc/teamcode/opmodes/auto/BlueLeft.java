package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Constants.Drive.StartPositions;
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

@Autonomous(name = "blue left", group = "blue")
public class BlueLeft extends Auton {
    ArrayList<Path> paths = new ArrayList<>();


    private Waypoint getPurplePlaceWaypoint() {
        double x = StartPositions.blueLeft.x;
        double y = 24;
        Rotation2d heading = new Rotation2d();
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                x -= 11.375;
                heading = Rotation2d.fromDegrees(45);
                break;
            case CENTER:
                y = 35;
                break;
            case RIGHT:
                x += 11.375;
                heading = Rotation2d.fromDegrees(-45);
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, heading, heading);
    }

    private Waypoint getYellowPlaceWaypoint() {
        double x = 28.0;
        double y = 34.5;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y -= 6.0;
                break;
            case CENTER:
                break;
            case RIGHT:
                y += 6.0;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius);
    }

    public BlueLeft() {
        super(Pipeline.Alliance.BLUE, StartPositions.blueLeft.rotation);
        paths.add(Path.getBuilder().setDefaultRadius(8).setTimeout(3000) // Drive to spike mark path
                .addWaypoint(StartPositions.blueLeft.toWaypoint())
                .addWaypoint(new FutureWaypoint(this::getPurplePlaceWaypoint)).build());
        paths.add(Path.getBuilder().setTimeout(2000) // Drive away from spike mark path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new FutureWaypoint(() -> {
                    Pose2d botPose = drive.odometry.getPose();
                    return new Waypoint(botPose.x - 8.0, botPose.y - 3.0, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(90));
                }))
                .build());
        paths.add(Path.getBuilder().setTimeout(5000).setDefaultMaxVelocity(0.5) // Drive to backdrop path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(new FutureWaypoint(this::getYellowPlaceWaypoint))
                .build());
        paths.add(Path.getBuilder().setTimeout(5000) // Park path
                .addWaypoint(new FutureWaypoint(() -> drive.odometry.getPose().toWaypoint()))
                .addWaypoint(36.0, 30)
                .addWaypoint(33.0, 25)
                .addWaypoint(new Waypoint(18, 10, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(90)))
                .build());
    }

    @Override
    public void start() {
        super.start();
        autonomousCommand = SequentialCommandGroup.getBuilder()
                .add(new FollowPath(paths.get(0), drive)) // Drive to place the purple pixel
                .add(new TimedIntake(intake, -1, 2000)) // Run the intake in reverse to spit out the purple pixel
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(8)
                        .addWaypoint(0, 15)
                        .addWaypoint(new Waypoint(-30.0, 30.0, 8.0)).build(), drive))
                .add(new ParallelCommandGroup( // Drive away from the spike mark and extend the slide
                        new FollowPath(paths.get(1), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(1500),
                                new SlideToPosition(slide, gamepad2, 500))))
                .add(new FollowPath(paths.get(2), drive)) // Drive to the backdrop
                .add(new InstantCommand(() -> placer.open())) // Place the pixel
                .add(new WaitCommand(500))
                .add(new ParallelCommandGroup( // Close the placer, stow the slide, and drive away
                        new FollowPath(paths.get(3), drive),
                        new SequentialCommandGroup(
                                new WaitCommand(750),
                                new ParallelCommandGroup(new InstantCommand(() -> placer.close()), new SlideToPosition(slide, gamepad2, 0)))
                ))
                .build();
    }

    @Override
    public void loop() {
        super.loop();
    }
}
