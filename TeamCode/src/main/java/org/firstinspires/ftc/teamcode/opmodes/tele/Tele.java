package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.ClimbDefault;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.DriveToBackboard;
import org.firstinspires.ftc.teamcode.commands.RunIntake;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.utils.GamepadHelper;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;

@TeleOp(name = "TeleOp")
public class Tele extends Robot {

    GamepadHelper driveController;
    GamepadHelper payloadController;

    public static Pose2d blueBackdropPose = new Pose2d();
    public static Pose2d redBackdropPose = new Pose2d();

    public static Pose2d backdropPose = blueBackdropPose;

    Command automaticPlace;

    @Override
    public void init() {
        super.init();
        if (alliance == Alliance.RED) {
            backdropPose = redBackdropPose;
        }
        automaticPlace = getAutomaticPlaceCommand(backdropPose);
        driveController = new GamepadHelper(gamepad1);
        payloadController = new GamepadHelper(gamepad2);
        drive.setDefaultCommand(new DriveDefault(drive, () -> -driveController.leftStickY.getAsDouble(), driveController.leftStickX, () -> -driveController.rightStickX.getAsDouble()));
        driveController.leftBumper.onTrue(new InstantCommand(() -> drive.enableSlowMode()));
        driveController.rightBumper.onTrue(new InstantCommand(() -> drive.enableFastMode()));
        drive.odometry.setPosition(pose); // Set the robot position to the last position of the robot in autonomous
        intake.setDefaultCommand(new RunIntake(intake, Constants.Intake.idleSpeed));
        payloadController.rightTrigger.whileTrue(new RunIntake(intake, 0.8));
        payloadController.leftTrigger.andNot(payloadController.rightBumper).whileTrue(new RunIntake(intake, -0.8));
        Command shootDrone = new SequentialCommandGroup(
                new InstantCommand(() -> droneShooter.angleAdjuster.setPosition(0.5)),
                new WaitCommand(1000),
                new InstantCommand(() -> droneShooter.release.setPosition(-0.5))
        );
        payloadController.a.onTrue(shootDrone);
        slide.setDefaultCommand(new SlideDefault(slide, () -> -payloadController.rightStickY.getAsDouble()));
        payloadController.rightBumper.onTrue(new SlideToPosition(slide, Constants.Slide.defaultPlacePosition));
        payloadController.leftBumper.onTrue(new SlideToPosition(slide, 0));
        payloadController.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadController.b.onTrue(new InstantCommand(() -> placer.close()));
        climber.setDefaultCommand(new ClimbDefault(climber, payloadController.leftStickY));
        driveController.rightBumper.onTrue(new DriveToBackboard(drive, lights, gamepad1));

        aprilTagCamera.enable(); // Remove this line after testing
    }

}
