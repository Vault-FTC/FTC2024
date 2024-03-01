package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.Constants.Vision;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Vector2d;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lights;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Robot extends OpMode {

    public Drive drive;
    public Intake intake;
    public Slide slide;
    public Placer placer;
    public Lights lights;

    public static Pose2d pose = null;
    VisionPortal visionPortal;
    private boolean cameraEnabled = false;
    private boolean usingCamera = false;
    AprilTagProcessor aprilTagProcessor;

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
        lights = new Lights(hardwareMap.get(RevBlinkinLedDriver.class, "lights"));

        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(2);
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "AprilCam"))
                .addProcessor(aprilTagProcessor)
                .build();
        setCameraExposure(6, 250);
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        useCamera();
    }

    public void enableAprilTagCamera() {
        cameraEnabled = true;
    }

    public void disableAprilTagCamera() {
        cameraEnabled = false;
    }

    public void useCamera() {
        boolean withinRange = Math.abs(Rotation2d.signed_minusPI_to_PI(pose.rotation.getAngleRadians())) < Math.toRadians(Vision.turnCamOnThresholdDegrees) && drive.odometry.getPose().x < Vision.useAprilTagMaxDistIn;
        if (withinRange && cameraEnabled) {
            if (!usingCamera) {
                usingCamera = true;
                visionPortal.resumeStreaming();
            }
            adjustBotPose();
        } else if (usingCamera) {
            usingCamera = false;
            visionPortal.stopStreaming();
        }
    }

    private static Pose2d calculateBotPose(AprilTagDetection detection) { // The provided angles are in degrees, and intrinsic
        Pose2d tagPose = Vision.backdropTagPoses[detection.id - 1];
        Pose2d camPose = new Pose2d(
                tagPose.x - detection.ftcPose.x,
                tagPose.y - detection.ftcPose.y,
                Vision.useAprilTagHeading && Math.abs(detection.ftcPose.yaw) < Vision.aprilTagHeadingThresholdDegrees ? Rotation2d.fromDegrees(tagPose.rotation.getAngleDegrees() - detection.ftcPose.yaw) : pose.rotation);
        Vector2d relativeBotCoordinates = Vision.camToRobot.rotate(camPose.rotation.getAngleRadians());
        return new Pose2d(camPose.x + relativeBotCoordinates.x, camPose.y + relativeBotCoordinates.y, camPose.rotation);
    }

    public void adjustBotPose() {
        Vector2d position = new Vector2d();
        ArrayList<Rotation2d> rotations = new ArrayList<>();
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        int i = 0;
        for (AprilTagDetection detection : detections) {
            if (detection.metadata != null && detection.ftcPose.range < Vision.useAprilTagMaxDistIn && detection.id >= 1 && detection.id <= 6) {
                Pose2d calculatedPose = calculateBotPose(detection);
                position.add(calculatedPose);
                rotations.add(calculatedPose.rotation);
                i++;
            }
        }
        if (i == 0) { // If none of the desired tags are detected, do nothing
            return;
        }

        drive.odometry.setPosition(new Pose2d(position.multiply((double) 1 / i), Rotation2d.averageRotations(rotations.toArray(new Rotation2d[]{}))));
    }

    private void setCameraExposure(int exposureMS, int gain) {
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
        }
        exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
    }

}
