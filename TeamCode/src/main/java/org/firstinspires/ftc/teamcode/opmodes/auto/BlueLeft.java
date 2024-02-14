package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commands.TimedIntake;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline.PropLocation;

import java.util.ArrayList;

@Autonomous(name = "blue left", group = "blue")
public class BlueLeft extends Auton {
    ArrayList<FollowPath> placePurplePixel = new ArrayList<>();

    public BlueLeft() {
        super(AutonType.BLUE_LEFT);
        placePurplePixel.add(new FollowPath(Path.getBuilder().setDefaultRadius(8).addWaypoint(0, 0).addWaypoint(-15, 15).build(), drive));
        placePurplePixel.add(new FollowPath(Path.getBuilder().setDefaultRadius(8).addWaypoint(0, 0).addWaypoint(0, 15).build(), drive));
        placePurplePixel.add(new FollowPath(Path.getBuilder().setDefaultRadius(8).addWaypoint(0, 0).addWaypoint(15, 15).build(), drive));
    }

    @Override
    public void start() {
        PropLocation propLocation = visionPipeline.getPropLocation();
        autonomousCommand = SequentialCommandGroup.getBuilder()
                .add(placePurplePixel.get(propLocation.location))
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
}
