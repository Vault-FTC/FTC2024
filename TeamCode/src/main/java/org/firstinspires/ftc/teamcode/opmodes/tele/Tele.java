package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.AutomaticDroneLaunch;
import org.firstinspires.ftc.teamcode.commands.ClimbDefault;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.IntakeDefault;
import org.firstinspires.ftc.teamcode.commands.RunIntake;
import org.firstinspires.ftc.teamcode.commands.SlideCalibrate;
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

    public static Pose2d blueBackdropPose = new Pose2d(17.0, 30, new Rotation2d(-Math.PI / 2));
    public static Pose2d redBackdropPose = new Pose2d(17.0, 110, new Rotation2d(-Math.PI / 2));

    public static Pose2d backdropPose = blueBackdropPose;

    Command automaticPlace;

    @Override
    public void init() {
        // TODO: update this project to the latest SDK version

        super.init();
        aprilTagCamera.enable();
        if (alliance == Alliance.RED) {
            backdropPose = redBackdropPose;
        }
        automaticPlace = getAutomaticPlaceCommand(backdropPose.toWaypoint());
        driveController = new GamepadHelper(gamepad1);
        payloadController = new GamepadHelper(gamepad2);

        drive.setDefaultCommand(new DriveDefault(drive, () -> -driveController.leftStickY.getAsDouble(), () -> driveController.leftStickX.getAsDouble(), () -> -driveController.rightStickX.getAsDouble()));
        driveController.leftBumper.onTrue(new InstantCommand(() -> drive.enableSlowMode()));
        driveController.rightBumper.onTrue(new InstantCommand(() -> drive.enableFastMode()));
        driveController.a.and(driveController.b).onTrue(automaticPlace);
        driveController.b.and(driveController.x).and(driveController.y).onTrue(new InstantCommand(() -> drive.odometry.setPosition(new Pose2d())));
        if (botPose == null) {
            botPose = new Pose2d();
        }
        drive.odometry.setPosition(botPose); // Set the robot position to the last position of the robot in autonomous
        drive.setFieldCentricOffset(fieldCentricOffset);

        intake.setDefaultCommand(new IntakeDefault(intake, lights, drive.odometry::getPose)); // Runs the intake automatically when the robot is in the right spot
        payloadController.rightTrigger.or(driveController.rightTrigger).whileTrue(new RunIntake(intake, Constants.Intake.defaultSpeed));
        payloadController.leftTrigger.or(driveController.leftTrigger).whileTrue(new RunIntake(intake, -Constants.Intake.defaultSpeed));

        Command shootDrone = new SequentialCommandGroup(
                new InstantCommand(() -> droneShooter.angleAdjuster.setPosition(0.6)),
                new WaitCommand(2000),
                new InstantCommand(() -> {
                    droneShooter.release.setPosition(0.8
                    );
                })
        );
        payloadController.y.onTrue(shootDrone);
        payloadController.x.onTrue(new AutomaticDroneLaunch(drive, shootDrone, gamepad1));

        slide.setDefaultCommand(new SlideDefault(slide, () -> -payloadController.rightStickY.getAsDouble()));
        payloadController.rightBumper.onTrue(new SlideToPosition(slide, Constants.Slide.defaultPlacePosition, gamepad2));
        payloadController.leftBumper.onTrue(new SlideToPosition(slide, 0, gamepad2));
        payloadController.options.whileTrue(new SlideCalibrate(slide));
        payloadController.options.and(payloadController.b).onTrue(new InstantCommand(() -> slide.encoder.reset()));
        slide.encoder.setPosition(slidePose); // Set the slide position to the last slide position in autonomous

        payloadController.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadController.b.onTrue(new InstantCommand(() -> placer.close()));

        driveController.a.onTrue(new InstantCommand(() -> drive.odometry.setPosition(new Pose2d())));

        climber.setDefaultCommand(new ClimbDefault(climber, payloadController.leftStickY));
        // payloadController.dpadUp.onTrue(new InstantCommand(() -> climber.deliverHook()));
        //payloadController.dpadDown.onTrue(new SlideToPosition(slide, Constants.Slide.defaultPlacePosition));

        payloadController.dpadDown.onTrue(new InstantCommand(() -> placer.storagePosition()));
        payloadController.dpadUp.onTrue(new InstantCommand(() -> placer.placePosition()));

        //payloadController.dpadLeft.onTrue(new InstantCommand(() -> placer.open()));
        //payloadController.dpadRight.onTrue(new InstantCommand(() -> placer.close()));
    }

    public void start() {
        //droneShooter.angleAdjuster.setPosition(0.45);
        //droneShooter.release.setPosition(0.0);
    }

    public void loop() {
        super.loop();
        telemetry.addData("servo pose", droneShooter.angleAdjuster.getPosition());
        telemetry.addData("cam", aprilTagCamera.cameraEnabled);
    }

}
