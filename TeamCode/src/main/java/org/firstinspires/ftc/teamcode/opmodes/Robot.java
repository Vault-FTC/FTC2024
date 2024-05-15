package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FlashLights;
import org.firstinspires.ftc.teamcode.commands.SlideDefault;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.Trigger;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.FollowPathCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.rustboard.RustboardLayout;
import org.firstinspires.ftc.teamcode.rustboard.Server;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagCamera;
import org.firstinspires.ftc.teamcode.subsystems.Climber;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.DroneShooter;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lights;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.PurplePixelPlacer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.utils.SuperGamepad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Robot extends OpMode {
    public LynxModule controlHub;
    public LynxModule expansionHub;
    public SuperGamepad driveController;
    public SuperGamepad payloadController;
    public Drive drive;
    public Intake intake;
    public Slide slide;
    public Placer placer;
    public Climber climber;
    public Lights lights;
    public AprilTagCamera aprilTagCamera;
    public DroneShooter droneShooter;
    public PurplePixelPlacer purplePixelPlacer;
    public static Pose2d botPose = null;
    public static Pose2d blueBackdropPose = new Pose2d(17.0, 30, new Rotation2d(-Math.PI / 2));
    public static Pose2d redBackdropPose = new Pose2d(17.0, 110, new Rotation2d(-Math.PI / 2));
    public static int slidePose = 0;
    public static Rotation2d fieldCentricOffset = new Rotation2d();

    public enum Alliance {
        BLUE,
        RED
    }

    public enum GameElementLocation {
        LEFT,
        CENTER,
        RIGHT
    }

    public static Alliance alliance = Alliance.BLUE;

    private void setup() {
        if (botPose == null) {
            botPose = new Pose2d(0, 0, new Rotation2d(-Math.PI));
        }

        // Instantiate the gamepad helpers
        driveController = new SuperGamepad(gamepad1);
        payloadController = new SuperGamepad(gamepad2);

        // Instantiate subsystems
        drive = new Drive(hardwareMap);
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        placer = new Placer(hardwareMap);
        slide = new Slide(hardwareMap, placer);
        slide.setDefaultCommand(new SlideDefault(slide, () -> -payloadController.rightStickY.getAsDouble()));
        climber = new Climber(hardwareMap);
        lights = new Lights(hardwareMap.get(RevBlinkinLedDriver.class, "lights"));
        aprilTagCamera = new AprilTagCamera(hardwareMap);
        aprilTagCamera.onDetect = () -> drive.odometry.setPosition(aprilTagCamera.getCalculatedBotPose());
        droneShooter = new DroneShooter(hardwareMap);
        purplePixelPlacer = new PurplePixelPlacer(hardwareMap);
    }


    @Override
    public void init() {
        CommandScheduler.getInstance().clearRegistry();
        CommandScheduler.getInstance().cancelAll();
        Server.getInstance().start();
        Server.getInstance().newLog();
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        if (hubs.get(0).isParent()) {
            controlHub = hubs.get(0);
            expansionHub = hubs.get(1);
        } else {
            controlHub = hubs.get(1);
            expansionHub = hubs.get(0);
        }
        setup();
    }

    @Override
    public void init_loop() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        RustboardLayout.setNodeValue("battery voltage", controlHub.getInputVoltage(VoltageUnit.VOLTS));
        botPose = drive.odometry.getPose();
        slidePose = slide.encoder.getPosition();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().clearRegistry();
        try {
            Server.getInstance().stop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getToBackdropPath(Waypoint backdropWaypoint) {
        Pose2d botPose = drive.odometry.getPose();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        waypoints.add(botPose.toWaypoint());
        double timeout;
        if (botPose.x > 85.0) { // If robot is in front of rigging
            waypoints.add(new Waypoint(90, 70, DriveConstants.defaultFollowRadius));
            waypoints.add(new Waypoint(65, 70, DriveConstants.defaultFollowRadius));
        }
        timeout = 5000;
        double initialOffset = 20.0;
        if (botPose.x > backdropWaypoint.x + initialOffset) {
            waypoints.add(new Waypoint(backdropWaypoint.x + initialOffset, backdropWaypoint.y, DriveConstants.defaultFollowRadius, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90)));
            timeout += 2000;
        }
        double offset = 6.0;
        if (botPose.x > backdropWaypoint.x + offset) {
            waypoints.add(new Waypoint(backdropWaypoint.x + offset, backdropWaypoint.y, DriveConstants.defaultFollowRadius, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90)));
            timeout += 1000;
        }
        return new Path(timeout, waypoints.toArray(new Waypoint[]{}));
    }

    public Command getAutomaticPlaceCommand(Waypoint backdropWaypoint) {
        Command flashLights = new FlashLights(lights, 1000);
        Command command = new SequentialCommandGroup(
                new InstantCommand(flashLights::schedule),
                new InstantCommand(aprilTagCamera::enable),
                new FollowPathCommand(() -> getToBackdropPath(backdropWaypoint), drive), // Get close to the backdrop
                new SlideToPosition(slide, 1200),
                new WaitCommand(1500), // Wait for an april tag detection
                new InstantCommand(aprilTagCamera::disable),
                new BackdropHome(drive.base, slide, placer, backdropWaypoint, 2000, 500), // Home in on the backdrop
                new InstantCommand(() -> placer.open()),
                new FollowPathCommand(Path.getBuilder().addWaypoint(backdropWaypoint).addWaypoint(backdropWaypoint.x + 4.0, backdropWaypoint.y).build(), drive),
                new InstantCommand(flashLights::cancel));
        new Trigger(() -> !gamepad1.atRest()).onTrue(new InstantCommand(command::cancel));
        return command;
    }

}