package org.firstinspires.ftc.teamcode.opmodes.auto;

import static org.firstinspires.ftc.teamcode.Constants.fieldLengthIn;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commands.TimedIntake;
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

@Autonomous(name = "Red Right")
public class ConfigurableRedRight extends Auton {

    boolean goToStack;

    public ConfigurableRedRight() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redRight);
        goToStack = DashboardLayout.loadBoolean("stack_RR");
    }

    Path leftPath = Path.getBuilder().setTimeout(1500)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.redRight.x - 6, fieldLengthIn - 24)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x, fieldLengthIn - 29.5, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(45), 0.7))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(1500)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x, fieldLengthIn - 37, Constants.Drive.defaultFollowRadius, new Rotation2d(), new Rotation2d(), 0.7))
            .build();
    Path rightPath = Path.getBuilder().setTimeout(1500)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 11.375, fieldLengthIn - 26, Constants.Drive.defaultFollowRadius, null, new Rotation2d(), 0.7))
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
        double x = 18;
        double y = fieldLengthIn - 37;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = fieldLengthIn - 40;
                break;
            case CENTER:
                y = fieldLengthIn - 33.5;
                break;
            case RIGHT:
                y = fieldLengthIn - 26;
                break;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, new Rotation2d(-Math.PI / 2), new Rotation2d(-Math.PI / 2), 0.5);
    }


    @Override
    public void init() {
        super.init();
        new InstantCommand(() -> aprilTagCamera.enable());
        SequentialCommandGroup.Builder builder = SequentialCommandGroup.getBuilder()
                .add(new FollowFuturePath(this::getPhenomenomallyPerfectPurplePlacePath, drive))
                .add(new InstantCommand(purplePixelPlacer::place)) // Place the pixel
                .add(new WaitCommand(DashboardLayout.loadDouble("wait_time_RR", 0)))
                .add(new ParallelCommandGroup( // Begin driving away and retract the dropper arm
                        new SequentialCommandGroup(new WaitCommand(250), new InstantCommand(purplePixelPlacer::retract)),
                        new FollowPath(Path.loadPath("to_backdrop_RR"), drive)))
                .add(new SlideToPosition(slide, Constants.Slide.autoPlacePosition))
                .add(new DelayUntil(slide::atTargetPosition, 1500))
                .add(new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 2000, 500))
                .add(new InstantCommand(placer::open))
                .add(new WaitCommand(250));
        if (goToStack) {
            builder.add(new ParallelCommandGroup( // Begin driving away and stow the slide
                            new SequentialCommandGroup(
                                    new WaitCommand(500),
                                    new SlideToPosition(slide, Constants.Slide.stowedPosition)
                            ),
                            new FollowPath(Path.loadPath("to_stack_RR"), drive)))
                    .add(new TimedIntake(intake, Constants.Intake.defaultSpeed, 1000))
                    .add(new FollowPath(Path.loadPath("from_stack_RR"), drive));
        } else {
            builder.add(new ParallelCommandGroup( // Begin driving away and stow the slide
                    new SequentialCommandGroup(
                            new WaitCommand(500),
                            new SlideToPosition(slide, Constants.Slide.stowedPosition)
                    ),
                    new FollowPath(Path.loadPath("park_RR"), drive)));
        }
        autonomousCommand = builder.build();
    }

}
