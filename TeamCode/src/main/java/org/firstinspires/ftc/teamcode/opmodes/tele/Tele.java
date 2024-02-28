package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.DriveToBackboard;
import org.firstinspires.ftc.teamcode.commands.IntakeDefault;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.utils.GamepadTriggers;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

@TeleOp(name = "Robot")
public class Tele extends Robot {

    GamepadTriggers driveControllerTriggers;
    GamepadTriggers payloadControllerTriggers;

    public static Pose2d blueBackdropPose = new Pose2d();
    public static Pose2d redBackdropPose = new Pose2d();

    public static Pose2d backdropPose = redBackdropPose;

    @Override
    public void init() {
        super.init();
        WebdashboardServer.getInstance(); // Initialize the dashboard server
        driveControllerTriggers = new GamepadTriggers(gamepad1);
        payloadControllerTriggers = new GamepadTriggers(gamepad2);
        drive.setDefaultCommand(new DriveDefault(drive, () -> gamepad1.left_stick_y, () -> -gamepad1.left_stick_x, () -> -gamepad1.right_stick_x));
        drive.odometry.setPosition(pose); // Set the robot position to the last position of the robot in autonomous
        intake.setDefaultCommand(new IntakeDefault(intake, Constants.Intake.idleSpeed));
        payloadControllerTriggers.rightTrigger.onTrue(new InstantCommand(intake, () -> intake.run(1)));
        payloadControllerTriggers.leftTrigger.andNot(payloadControllerTriggers.rightBumper).onTrue(new InstantCommand(intake, () -> intake.run(-1)));
        slide.setDefaultCommand(new SlideDefault(slide, () -> gamepad2.right_stick_y));
        payloadControllerTriggers.rightBumper.onTrue(new SlideToPosition(slide, gamepad2, Constants.Slide.defaultPlacePosition));
        payloadControllerTriggers.leftBumper.onTrue(new SlideToPosition(slide, gamepad2, 0));
        payloadControllerTriggers.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadControllerTriggers.b.onTrue(new InstantCommand(() -> placer.close()));
        driveControllerTriggers.rightBumper.onTrue(new DriveToBackboard(drive, gamepad1));
    }

}
