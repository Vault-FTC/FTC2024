package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.DelayUntil;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.FutureWaypoint;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

@Autonomous(name = "Blue Left")
public class ConfigurableBlueLeft extends Auton {

    boolean goToStack;

    public ConfigurableBlueLeft() {
        super(Pipeline.Alliance.BLUE, Constants.Drive.StartPositions.blueLeft);
        goToStack = DashboardLayout.loadBoolean("stack_BL");
    }

    Path leftPath = Path.getBuilder()
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.blueLeft.x - 11.375, 26, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(180), 0.8))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.blueLeft.x, 33, Constants.Drive.defaultFollowRadius, Rotation2d.fromDegrees(180), Rotation2d.fromDegrees(180), 0.7))
            .build();

    Path rightPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.blueLeft.x - 6, 24)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.blueLeft.x, 29.5, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(135), 0.8))
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
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90), 3);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = new SequentialCommandGroup(
                //new FollowFuturePath(this::getPhenomenallyPerfectPurplePlacePath, drive), // Drive to the spike mark
                new InstantCommand(purplePixelPlacer::place), // Place the pixel
                new WaitCommand(DashboardLayout.loadDouble("wait_time_BL", 1000)), // Wait for the pixel to drop
                new ParallelCommandGroup( // Begin driving away and retract the dropper arm
                        new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(purplePixelPlacer::retract)),
                        new FollowPath(Path.loadPath("to_backdrop_BL"), drive)),
                new SlideToPosition(slide, Constants.Slide.autoPlacePosition), // Move the slide to the correct position
                new DelayUntil(slide::atTargetPosition, 3000), // Wait until the slide is close to the correct position
                new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 2000, 500), // Home in on the backdrop
                new InstantCommand(placer::open), // Drop the yellow pixel
                new WaitCommand(750), // Wait for the pixel to drop
                new ParallelCommandGroup( // Begin driving away and stow the slide
                        new SequentialCommandGroup(
                                new WaitCommand(750),
                                new SlideToPosition(slide, Constants.Slide.stowedPosition)
                        ),
                        new FollowPath(Path.loadPath("park_BL"), drive)
                )
        );
    }

}
