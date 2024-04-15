package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

@Autonomous(name = "test")
public class ConfigurableBlueLeft extends Auton {
    public ConfigurableBlueLeft() {
        super(Pipeline.Alliance.BLUE, Constants.Drive.StartPositions.blueLeft);
    }

    Path leftPath = Path.getBuilder()
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.blueLeft.x - 11.375, 26, Constants.Drive.defaultFollowRadius, null, new Rotation2d()))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.x, 33)
            .build();

    Path rightPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.x - 6, 24)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.blueLeft.x, 29.5, Constants.Drive.defaultFollowRadius, null, new Rotation2d(-Math.PI / 4)))
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

    @Override
    public void init() {
        super.init();
        autonomousCommand = new SequentialCommandGroup(
                new FollowFuturePath(() -> getPhenomenomallyPerfectPurplePlacePath(), drive),
                new InstantCommand(() -> purplePixelPlacer.place()),
                new WaitCommand(DashboardLayout.loadDouble("wait_time_BL", 1000)),
                new ParallelCommandGroup(
                        new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(() -> purplePixelPlacer.retract())),
                        new FollowPath(Path.loadPath("to_backdrop_BL"), drive)),
                new FollowPath(Path.loadPath("to_stack_BL"), drive)
                //new SlideToPosition(slide, Constants.Slide.defaultPlacePosition),
                //new BackdropHome(drive.base, slide, placer, blueBackdropPose.toWaypoint(), 1000, 5000)
        );
    }

    @Override
    public void start() {
        CommandScheduler.getInstance().schedule(autonomousCommand);
    }
}
