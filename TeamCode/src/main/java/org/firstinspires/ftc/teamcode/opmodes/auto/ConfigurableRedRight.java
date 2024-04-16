package org.firstinspires.ftc.teamcode.opmodes.auto;

import static org.firstinspires.ftc.teamcode.Constants.fieldLengthIn;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
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
public class ConfigurableRedRight extends Auton {

    boolean goToStack;

    public ConfigurableRedRight() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redRight);
        goToStack = DashboardLayout.loadBoolean("stack_RR");
    }
    Path leftPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.redRight.x - 6, fieldLengthIn - 24)
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x, fieldLengthIn - 29.5, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(-135)))
            .build();
    Path centerPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(Constants.Drive.StartPositions.redRight.x, fieldLengthIn - 35)
            .build();

    Path rightPath = Path.getBuilder().setTimeout(3000)
            .addWaypoint(Constants.Drive.StartPositions.redRight.toWaypoint())
            .addWaypoint(new Waypoint(Constants.Drive.StartPositions.redRight.x - 11.375, fieldLengthIn - 26, Constants.Drive.defaultFollowRadius, null, new Rotation2d(Math.PI)))
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
        return new Waypoint(waypoint.x + 6.0, waypoint.y, waypoint.followRadius, waypoint.targetFollowRotation, waypoint.targetEndRotation, 0.5);
    }

    private Waypoint getYellowPlaceWaypoint() {
        double x = 18;
        double y = fieldLengthIn - 37;
        switch (visionPipeline.getPropLocation()) {
            case LEFT:
                y = fieldLengthIn - 43;
                break;
            case CENTER:
                y = fieldLengthIn - 35.5;
                break;
            case RIGHT:
                y = fieldLengthIn - 29.5;
                break;
        }
        return new Waypoint(x, y, Constants.Drive.defaultFollowRadius, new Rotation2d(-Math.PI / 2), new Rotation2d(-Math.PI / 2), 0.5);
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = new SequentialCommandGroup(
                new FollowFuturePath(() -> getPhenomenomallyPerfectPurplePlacePath(), drive),
                new InstantCommand(() -> purplePixelPlacer.place()),
                new WaitCommand(DashboardLayout.loadDouble("wait_time_RR", 1000)),
                new ParallelCommandGroup(
                        new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(() -> purplePixelPlacer.retract())),
                        new FollowPath(Path.loadPath("to_backdrop_RR").appendWaypoint(new FutureWaypoint(this::getInitialYellowPlaceWaypoint), 5000), drive)),
                new SlideToPosition(slide, Constants.Slide.defaultPlacePosition),
                new BackdropHome(drive.base, slide, placer, new FutureWaypoint(this::getYellowPlaceWaypoint), 1000, 3000),
                new InstantCommand(() -> placer.open()),
                new WaitCommand(500),
                new ParallelCommandGroup(
                        new SequentialCommandGroup(
                                new WaitCommand(750),
                                new SlideToPosition(slide, Constants.Slide.stowedPosition)),
                        new FollowPath(Path.loadPath("park_RR"), drive)
                )
        );
    }

}
