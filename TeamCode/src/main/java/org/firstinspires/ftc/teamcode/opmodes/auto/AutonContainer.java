package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.DelayUntil;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.AutonomousCommand;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.FollowPathCommand;
import org.firstinspires.ftc.teamcode.drive.FutureInstance;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

public abstract class AutonContainer extends Auton {
    protected static Pose2d blueLeftStartPosition = new Pose2d(58.944, 7.916899, new Rotation2d(Math.PI));
    protected static Pose2d blueRightStartPosition = blueLeftStartPosition.translateX(DriveConstants.tileLengthIn * 2);
    protected static Pose2d redLeftStartPosition = blueRightStartPosition.mirror();
    protected static Pose2d redRightStartPosition = blueLeftStartPosition.mirror();
    protected AutonomousCommand blueLeftCommand = new AutonomousCommand(
            new SequentialCommandGroup(
                    new FollowPathCommand(this::getPurplePlacePath, drive), // Drive to the spike mark
                    new InstantCommand(purplePixelPlacer::place), // Place the pixel
                    new WaitCommand(DashboardLayout.loadDouble("wait_time_BL", 1000)), // Wait for the pixel to drop
                    new ParallelCommandGroup( // Begin driving away and retract the dropper arm
                            new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(purplePixelPlacer::retract)),
                            new FollowPathCommand(Path.loadPath("to_backdrop_BL"), drive)),
                    new SlideToPosition(slide, Constants.Slide.autoPlacePosition), // Move the slide to the correct position
                    new DelayUntil(slide::atTargetPosition, 2000), // Wait until the slide is close to the correct position
                    new BackdropHome(drive.base, slide, placer, new FutureInstance(this::getYellowPlaceWaypoint), 2000, 500), // Home in on the backdrop
                    new InstantCommand(placer::open), // Drop the yellow pixel
                    new WaitCommand(750), // Wait for the pixel to drop
                    new ParallelCommandGroup( // Begin driving away and stow the slide
                            new SequentialCommandGroup(
                                    new WaitCommand(750),
                                    new SlideToPosition(slide, Constants.Slide.stowedPosition)
                            ),
                            new FollowPathCommand(Path.loadPath("park_BL"), drive)
                    )
            )
    );
    protected AutonomousCommand blueRightCommand = new AutonomousCommand(
            new SequentialCommandGroup(
                    new InstantCommand(() -> aprilTagCamera.disable()),
                    new FollowPathCommand(this::getPurplePlacePath, drive), // Drive to the spike mark
                    new InstantCommand(purplePixelPlacer::place), // Place the pixel
                    new WaitCommand(DashboardLayout.loadDouble("wait_time_BR", 1000)), // Wait for the pixel to drop
                    new FollowPathCommand(Path.loadPath("back_up"), drive),
                    new ParallelCommandGroup( // Begin driving away and retract the dropper arm
                            new SequentialCommandGroup(new WaitCommand(1000), new InstantCommand(purplePixelPlacer::retract)),
                            new FollowPathCommand(Path.loadPath("to_backdrop_BR"), drive)),
                    new SlideToPosition(slide, Constants.Slide.autoPlacePosition), // Move the slide to the correct position
                    new DelayUntil(slide::atTargetPosition, 3000), // Wait until the slide is close to the correct position
                    new BackdropHome(drive.base, slide, placer, new FutureInstance<>(this::getYellowPlaceWaypoint), 2000, 500), // Home in on the backdrop
                    new InstantCommand(placer::open), // Drop the yellow pixel
                    new WaitCommand(750), // Wait for the pixel to drop
                    new ParallelCommandGroup( // Begin driving away and stow the slide
                            new SequentialCommandGroup(
                                    new WaitCommand(750),
                                    new SlideToPosition(slide, Constants.Slide.stowedPosition)
                            )
                    )
            )
    );
    protected AutonomousCommand redLeftCommand = blueRightCommand.mirrorPaths();
    protected AutonomousCommand redRightCommand = blueLeftCommand.mirrorPaths();

    public AutonContainer(Alliance alliance, Pose2d startPose) {
        super(alliance, startPose);
    }

    Path getPurplePlacePath() {
        return new Path();
    }

    Waypoint getYellowPlaceWaypoint() {
        return null;
    }

}