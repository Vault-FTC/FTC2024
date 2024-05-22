package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.commands.CameraCalibrate;
import org.firstinspires.ftc.teamcode.constants.Constants;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Vector2d;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.RustboardLayout;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Server;
import org.firstinspires.ftc.teamcode.org.rustlib.vision.CameraActivationZone;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTagCamera extends Subsystem {
    public final VisionPortal visionPortal;
    public boolean cameraEnabled = false;
    private boolean streaming = false;
    AprilTagProcessor aprilTagProcessor;
    Supplier<Pose2d> poseSupplier;
    private Pose2d calculatedPose = new Pose2d();
    public Runnable onDetect = () -> {
    };
    private final CameraActivationZone[] activationZones;

    public AprilTagCamera(HardwareMap hardwareMap, CameraActivationZone... activationZones) {
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(0);
        aprilTagProcessor.setPoseSolver(AprilTagProcessor.PoseSolver.OPENCV_IPPE_SQUARE);
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "backCam"))
                .addProcessor(aprilTagProcessor)
                .build();
        this.activationZones = activationZones;
        CommandScheduler.getInstance().schedule(new CameraCalibrate(this));
    }

    private Pose2d calculateBotPose(AprilTagDetection detection) { // The provided angles are in degrees, and intrinsic
        Pose2d tagPose = Constants.Vision.backdropTagPoses[detection.id - 1];
        Vector2d relativeCoordinates = new Vector2d(-detection.ftcPose.x, detection.ftcPose.y).rotate(tagPose.rotation.getAngleRadians());
        double detectionYaw = -detection.ftcPose.yaw; // Because the camera is upside-down
        Pose2d camPose = new Pose2d(
                tagPose.x - relativeCoordinates.x,
                tagPose.y - relativeCoordinates.y,
                Constants.Vision.useAprilTagHeading && Math.abs(detectionYaw) < Constants.Vision.aprilTagHeadingThresholdDegrees ? Rotation2d.fromDegrees(tagPose.rotation.getAngleDegrees() - detectionYaw) : new Rotation2d(poseSupplier.get().rotation.getAngleRadians() - Math.PI));
        Vector2d relativeBotCoordinates = Constants.Vision.camToRobot.rotate(camPose.rotation.getAngleRadians());
        return new Pose2d(camPose.x + relativeBotCoordinates.x, camPose.y + relativeBotCoordinates.y, new Rotation2d(camPose.rotation.getAngleRadians() + Math.PI));
    }

    private void adjustBotPose() {
        Vector2d position = new Vector2d();
        ArrayList<Rotation2d> rotations = new ArrayList<>();
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        int i = 0;
        for (AprilTagDetection detection : detections) {
            RustboardLayout.setNodeValue("follow", detection.id);
            if (detection.metadata != null && detection.id >= 1 && detection.id <= 6 && detection.ftcPose.range < Constants.Vision.useAprilTagMaxRangeIn) {
                Pose2d calculatedPose = calculateBotPose(detection);
                position = position.add(calculatedPose);
                rotations.add(calculatedPose.rotation);
                i++;
            }
        }
        if (i == 0) { // If none of the desired tags are detected, do nothing
            return;
        }
        calculatedPose = new Pose2d(position.multiply((double) 1 / i), Rotation2d.averageRotations(rotations.toArray(new Rotation2d[]{})));
        onDetect.run();
    }

    public void setExposure(int exposureMS, int gain) {
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
        }
        exposureControl.setExposure(exposureMS, TimeUnit.MILLISECONDS);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
    }

    public void enable() {
        cameraEnabled = true;
    }

    public void disable() {
        cameraEnabled = false;
    }

    public Pose2d getCalculatedBotPose() {
        return calculatedPose;
    }

    private void resumeStream() {
        try {
            visionPortal.resumeStreaming();
            streaming = true;
        } catch (RuntimeException e) {
            Server.log(e);
        }
    }

    private void stopStream() {
        try {
            visionPortal.stopStreaming();
            streaming = false;
        } catch (RuntimeException e) {
            Server.log(e);
        }
    }

    boolean withinZone() {
        boolean withinRange;
        if (activationZones.length == 0) {
            withinRange = true;
        } else {
            withinRange = false;
        }
        for (CameraActivationZone activationZone : activationZones) {
            if (activationZone.withinZone()) {
                withinRange = true;
                break;
            }
        }
        return withinRange;
    }

    @Override
    public void periodic() {
        if (cameraEnabled) {
            if (!streaming) {
                resumeStream();
            }
            adjustBotPose();
        } else if (streaming) {
            stopStream();
        }
    }

}
