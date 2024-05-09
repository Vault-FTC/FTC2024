package org.firstinspires.ftc.teamcode.opmodes.auto;

import static org.firstinspires.ftc.teamcode.Constants.fieldLengthIn;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.DelayUntil;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.FutureWaypoint;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

@Autonomous(name = "Red Right")
public class ConfigurableRedRight extends Auton {

    public ConfigurableRedRight() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redRight);
    }

    Path leftPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 6, 24, DriveConstants.defaultFollowRadius, new Rotation2d(), null))
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x + 6, 32, DriveConstants.defaultFollowRadius, null, Rotation2d.fromDegrees(45), 0.8))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x, 35.5, DriveConstants.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.7))
            .build();

    Path rightPath = Path.getBuilder()
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint()).setTimeout(3000)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 10.5, 30, DriveConstants.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.8))
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

    private Waypoint getYellowPlaceWaypoint() {
        double x = 17;
        double y = fieldLengthIn - 37;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = fieldLengthIn - 28;
                break;
            case CENTER:
                break;
            case RIGHT:
                y = fieldLengthIn - 41;
                break;
        }
        return new Waypoint(x, y, DriveConstants.defaultFollowRadius, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90));
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = new SequentialCommandGroup(
                new FollowFuturePath(this::getPhenomenallyPerfectPurplePlacePath, drive), // Drive to the spike mark
                new InstantCommand(purplePixelPlacer::place), // Place the pixel
                new WaitCommand(DashboardLayout.loadDouble("wait_time_RR", 1000)), // Wait for the pixel to drop
                new ParallelCommandGroup( // Begin driving away and retract the dropper arm
                        new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(purplePixelPlacer::retract)),
                        new FollowPath(Path.loadPath("to_backdrop_RR"), drive)),
                new SlideToPosition(slide, Constants.Slide.autoPlacePosition), // Move the slide to the correct position
                new DelayUntil(slide::atTargetPosition, 2000), // Wait until the slide is close to the correct position
                new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 2000, 500), // Home in on the backdrop
                new InstantCommand(placer::open), // Drop the yellow pixel
                new WaitCommand(750), // Wait for the pixel to drop
                new ParallelCommandGroup( // Begin driving away and stow the slide
                        new SequentialCommandGroup(
                                new WaitCommand(750),
                                new SlideToPosition(slide, Constants.Slide.stowedPosition)
                        ),
                        new FollowPath(Path.loadPath("park_RR"), drive)
                )
        );
    }

}
