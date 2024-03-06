package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.ClimbDefault;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.IntakeDefault;
import org.firstinspires.ftc.teamcode.commands.RunIntake;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.utils.GamepadHelper;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;

@TeleOp(name = "TeleOp")
public class Tele extends Robot {

    GamepadHelper driveController;
    GamepadHelper payloadController;

    public static Pose2d blueBackdropPose = new Pose2d(9.0, 40, new Rotation2d(Math.PI / 2));
    public static Pose2d redBackdropPose = new Pose2d(9.0, 110, new Rotation2d(Math.PI / 2));

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

        drive.setDefaultCommand(new DriveDefault(drive, () -> -driveController.leftStickY.getAsDouble(), () -> driveController.leftStickX.getAsDouble(), () -> -driveController.rightStickX.getAsDouble()));
        driveController.leftBumper.onTrue(new InstantCommand(() -> drive.enableSlowMode()));
        driveController.rightBumper.onTrue(new InstantCommand(() -> drive.enableFastMode()));
        driveController.a.and(driveController.b).onTrue(automaticPlace);
        drive.odometry.setPosition(pose); // Set the robot position to the last position of the robot in autonomous

        intake.setDefaultCommand(new IntakeDefault(intake, drive.odometry::getPose)); // Runs the intake automatically when the robot is in the right spot
        payloadController.rightTrigger.or(driveController.rightTrigger).whileTrue(new RunIntake(intake, Constants.Intake.defaultSpeed));
        payloadController.leftTrigger.or(driveController.leftTrigger).whileTrue(new RunIntake(intake, -Constants.Intake.defaultSpeed));

        Command shootDrone = new SequentialCommandGroup(
                new InstantCommand(() -> droneShooter.angleAdjuster.setPosition(0.65)),
                new WaitCommand(2000),
                new InstantCommand(() -> droneShooter.release.setPosition(0))
        );
        payloadController.y.onTrue(shootDrone);

        slide.setDefaultCommand(new SlideDefault(slide, () -> -payloadController.rightStickY.getAsDouble()));
        payloadController.rightBumper.onTrue(new SlideToPosition(slide, Constants.Slide.defaultPlacePosition, gamepad2));
        payloadController.leftBumper.onTrue(new SlideToPosition(slide, 0, gamepad2));

        payloadController.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadController.b.onTrue(new InstantCommand(() -> placer.close()));

        climber.setDefaultCommand(new ClimbDefault(climber, payloadController.leftStickY));
    }

    public void start() {
        droneShooter.angleAdjuster.setPosition(0.4);
    }

    public void loop() {
        super.loop();
        telemetry.addData("servo pose", droneShooter.angleAdjuster.getPosition());
        telemetry.addData("cam", aprilTagCamera.cameraEnabled);
    }

}
