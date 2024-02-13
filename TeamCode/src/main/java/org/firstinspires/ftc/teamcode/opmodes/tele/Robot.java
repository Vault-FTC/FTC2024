package org.firstinspires.ftc.teamcode.opmodes.tele;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.IntakeDefault;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.utils.GamepadTriggers;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

@TeleOp(name = "Robot")
public class Robot extends OpMode {

    GamepadTriggers driveControllerTriggers;
    GamepadTriggers payloadControllerTriggers;
    Drive drive;
    Intake intake;
    Slide slide;
    Placer placer;

    public enum State {
        INITIALIZING,
        INITIALIZED,
        AUTO,
        TELE,
        DISABLED
    }

    public static State robotState = State.DISABLED;

    @Override
    public void init() {
        WebdashboardServer.getInstance(); // Initialize the dashboard server
        driveControllerTriggers = new GamepadTriggers(gamepad1);
        payloadControllerTriggers = new GamepadTriggers(gamepad2);
        drive = new Drive(hardwareMap);
        drive.setDefaultCommand(new DriveDefault(drive, () -> gamepad1.left_stick_y, () -> -gamepad1.left_stick_x, () -> -gamepad1.right_stick_x));
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        intake.setDefaultCommand(new IntakeDefault(intake, Constants.Intake.idleSpeed));
        payloadControllerTriggers.rightTrigger.onTrue(new InstantCommand(intake, () -> intake.run(1)));
        payloadControllerTriggers.leftTrigger.andNot(payloadControllerTriggers.rightBumper).onTrue(new InstantCommand(intake, () -> intake.run(-1)));
        slide = new Slide(hardwareMap.get(DcMotor.class, "slideMotor"), hardwareMap.get(TouchSensor.class, "limit"));
        slide.setDefaultCommand(new SlideDefault(slide, () -> gamepad2.right_stick_y));
        payloadControllerTriggers.rightBumper.onTrue(new SlideToPosition(slide, gamepad2, Constants.Slide.defaultPlacePosition));
        payloadControllerTriggers.leftBumper.onTrue(new SlideToPosition(slide, gamepad2, 0));
        placer = new Placer(hardwareMap);
        payloadControllerTriggers.a.onTrue(new InstantCommand(() -> placer.open()));
        payloadControllerTriggers.b.onTrue(new InstantCommand(() -> placer.close()));
        robotState = State.INITIALIZED;
    }

    @Override
    public void start() {
        robotState = State.TELE;
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void stop() {
        robotState = State.DISABLED;
    }
}
