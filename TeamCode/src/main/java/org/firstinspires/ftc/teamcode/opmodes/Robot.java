package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
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

public class Robot extends OpMode {

    public Drive drive;
    public Intake intake;
    public Slide slide;
    public Placer placer;
    public Climber climber;
    public Lights lights;
    public AprilTagCamera aprilTagCamera;
    public DroneShooter droneShooter;

    public static Pose2d pose = null;

    public static Alliance alliance = Alliance.BLUE;

    @Override
    public void init() {
        if (pose == null) {
            pose = new Pose2d();
        }
        CommandScheduler.getInstance().clearRegistry();
        WebdashboardServer.getInstance(); // Initialize the dashboard server

        // Instantiate subsystems
        drive = new Drive(hardwareMap);
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        slide = new Slide(
                hardwareMap.get(DcMotor.class, "slideMotor1"),
                hardwareMap.get(DcMotor.class, "slideMotor2"),
                hardwareMap.get(TouchSensor.class, "limit"));
        placer = new Placer(hardwareMap);
        climber = new Climber(hardwareMap.get(DcMotor.class, "climbMotor"));
        lights = new Lights(hardwareMap.get(RevBlinkinLedDriver.class, "lights"));
        aprilTagCamera = new AprilTagCamera(hardwareMap, drive.odometry::getPose);
        aprilTagCamera.onDetect = () -> drive.odometry.setPosition(aprilTagCamera.getCalculatedPose());
        droneShooter = new DroneShooter(hardwareMap);
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }

}
