package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.ClimbDefault;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.DriveToBackboard;
import org.firstinspires.ftc.teamcode.commands.IntakeDefault;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.utils.GamepadHelper;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;

@TeleOp(name = "Robot")
public class Tele extends Robot {

    GamepadHelper driveController;
    GamepadHelper payloadController;

    public static Pose2d blueBackdropPose = new Pose2d();
    public static Pose2d redBackdropPose = new Pose2d();

    public static Pose2d backdropPose = blueBackdropPose;

    @Override
    public void init() {
        super.init();
        if (alliance == Alliance.RED) {
            backdropPose = redBackdropPose;
        }
        driveController = new GamepadHelper(gamepad1);
        payloadController = new GamepadHelper(gamepad2);
        drive.setDefaultCommand(new DriveDefault(drive, driveController.leftStickY, driveController.leftStickX, () -> -driveController.rightStickX.getAsDouble()));
        driveController.leftBumper.onTrue(new InstantCommand(() -> drive.enableSlowMode()));
        driveController.rightBumper.onTrue(new InstantCommand(() -> drive.enableFastMode()));
        drive.odometry.setPosition(pose); // Set the robot position to the last position of the robot in autonomous
        intake.setDefaultCommand(new IntakeDefault(intake, Constants.Intake.idleSpeed));
        payloadController.rightTrigger.onTrue(new InstantCommand(intake, () -> intake.run(1)));
        payloadController.leftTrigger.andNot(payloadController.rightBumper).onTrue(new InstantCommand(intake, () -> intake.run(-1)));
        slide.setDefaultCommand(new SlideDefault(slide, payloadController.rightStickY));
        payloadController.rightBumper.onTrue(new SlideToPosition(slide, gamepad2, Constants.Slide.defaultPlacePosition));
        payloadController.leftBumper.onTrue(new SlideToPosition(slide, gamepad2, 0));
        payloadController.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadController.b.onTrue(new InstantCommand(() -> placer.close()));
        climber.setDefaultCommand(new ClimbDefault(climber, payloadController.leftStickY));
        driveController.rightBumper.onTrue(new DriveToBackboard(drive, lights, gamepad1));

        enableAprilTagCamera(); // Remove this line after testing
    }

}
