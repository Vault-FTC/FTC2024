package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.BackdropHome;
import org.firstinspires.ftc.teamcode.commands.FlashLights;
import org.firstinspires.ftc.teamcode.commands.FollowFuturePath;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commands.SlideToPosition;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.InstantCommand;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.Trigger;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.drive.WaypointGenerator;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagCamera;
import org.firstinspires.ftc.teamcode.subsystems.Climber;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.DroneShooter;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lights;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

import java.util.ArrayList;

public class Robot extends OpMode {

    public Drive drive;
    public Intake intake;
    public Slide slide;
    public Placer placer;
    public Climber climber;
    public Lights lights;
    public AprilTagCamera aprilTagCamera;
    public DroneShooter droneShooter;

    public static Pose2d botPose = null;
    public static int slidePose = 0;

    public static Alliance alliance = Alliance.BLUE;

    @Override
    public void init() {
        if (botPose == null) {
            botPose = new Pose2d();
        }
        CommandScheduler.getInstance().clearRegistry();
        CommandScheduler.getInstance().cancelAll();
        WebdashboardServer.getInstance(); // Initialize the dashboard server

        // Instantiate subsystems
        drive = new Drive(hardwareMap);
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        slide = new Slide(
                hardwareMap.get(DcMotor.class, "slideMotor1"),
                hardwareMap.get(DcMotor.class, "slideMotor2"),
                hardwareMap.get(TouchSensor.class, "limit"), true);
        placer = new Placer(hardwareMap);
        climber = new Climber(hardwareMap);
        lights = new Lights(hardwareMap.get(RevBlinkinLedDriver.class, "lights"));
        aprilTagCamera = new AprilTagCamera(hardwareMap, drive.odometry::getPose);
        aprilTagCamera.onDetect = () -> drive.odometry.setPosition(aprilTagCamera.getCalculatedPose());
        droneShooter = new DroneShooter(hardwareMap);
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        botPose = drive.odometry.getPose();
        slidePose = slide.encoder.getPosition();
    }

    private Path getToBackdropPath(Waypoint backdropWaypoint) {
        Pose2d botPose = drive.odometry.getPose();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        waypoints.add(botPose.toWaypoint());
        if (drive.odometry.getPose().x > 85.0) { // If robot is in front of rigging
            waypoints.add(new Waypoint(90, 70, Constants.Drive.defaultFollowRadius));
            waypoints.add(new Waypoint(65, 70, Constants.Drive.defaultFollowRadius));
        }
        double backdropXInitial = 20.0;
        if (botPose.x > backdropWaypoint.x + backdropXInitial) {
            waypoints.add(new Waypoint(backdropWaypoint.x + backdropXInitial, backdropWaypoint.y, Constants.Drive.defaultFollowRadius));
        }
        waypoints.add(new Waypoint(backdropWaypoint.x + 4.0, backdropWaypoint.y, Constants.Drive.defaultFollowRadius, null, backdropWaypoint.targetEndRotation));
        return new Path(waypoints.toArray(new WaypointGenerator[]{}));
    }

    public Command getAutomaticPlaceCommand(Waypoint backdropWaypoint) {
        Command flashLights = new FlashLights(lights, 750);
        Command command = new SequentialCommandGroup(
                new InstantCommand(flashLights::schedule),
                new FollowFuturePath(() -> getToBackdropPath(backdropWaypoint), drive), // Get close to the backdrop
                new SlideToPosition(slide, 500),
                new InstantCommand(aprilTagCamera::enable),
                new WaitCommand(500), // Wait for an april tag detection
                new InstantCommand(aprilTagCamera::disable),
                new BackdropHome(drive.base, slide, placer, backdropWaypoint, 2000, 500), // Home in on the backdrop
                new InstantCommand(() -> placer.open()),
                new FollowPath(Path.getBuilder().addWaypoint(backdropWaypoint).addWaypoint(backdropWaypoint.x + 4.0, backdropWaypoint.y).build(), drive),
                new InstantCommand(flashLights::cancel));
        new Trigger(() -> !gamepad1.atRest()).onTrue(new InstantCommand(command::cancel));
        return command;
    }

}
