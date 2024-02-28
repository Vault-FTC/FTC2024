package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
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

@Autonomous(name = "blue left", group = "blue")
public class BlueLeft extends Auton {
    ArrayList<Path> paths = new ArrayList<>();


    private Waypoint getPurplePlaceWaypoint() {
        double x = 0;
        double y = 26;
        Rotation2d heading = new Rotation2d();
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                x = -6;
                y = 28;
                heading = Rotation2d.fromDegrees(45);
                break;
            case CENTER:
                x = 26;
                y = 26;
                break;
            case RIGHT:
                x = 6;
                y = 28;
                heading = Rotation2d.fromDegrees(-45);
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, heading, heading);
    }

    private Waypoint getYellowPlaceWaypoint() {
        double x = -36;
        double y = 26;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = 18;
                break;
            case CENTER:
                y = 26;
                break;
            case RIGHT:
                y = 34;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius);
    }

    public BlueLeft() {
        super(Pipeline.Alliance.BLUE);
        paths.add(Path.getBuilder().setDefaultRadius(8).addWaypoint(0, 0).addWaypoint(new FutureWaypoint(this::getPurplePlaceWaypoint)).build());
        paths.add(Path.getBuilder().addWaypoint(new FutureWaypoint(this::getYellowPlaceWaypoint)).build());

    }

    @Override
    public void start() {
        super.start();
        autonomousCommand = SequentialCommandGroup.getBuilder()
                .add(new FollowPath(paths.get(0), drive))
                .add(new TimedIntake(intake, -1, 2000))
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(8)
                        .addWaypoint(0, 15)
                        .addWaypoint(new Waypoint(-30.0, 30.0, 8.0)).build(), drive))
                .add(new SlideToPosition(slide, gamepad2, 500))
                .add(new InstantCommand(() -> placer.open()))
                .add(new WaitCommand(500))
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(8)
                        .addWaypoint(-30, 30)
                        .addWaypoint(new Waypoint(-25, 30, 8)).build(), drive))
                .add(new ParallelCommandGroup(new InstantCommand(() -> placer.close()), new SlideToPosition(slide, gamepad2, 0)))
                .build();
        super.start();
    }

    @Override
    public void loop() {
        super.loop();
    }
}
