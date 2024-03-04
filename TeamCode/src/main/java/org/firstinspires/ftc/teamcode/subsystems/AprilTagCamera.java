package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.CameraCalibrate;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Vector2d;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTagCamera extends Subsystem {

    public final VisionPortal visionPortal;
    private boolean cameraEnabled = false;
    private boolean usingCamera = false;
    AprilTagProcessor aprilTagProcessor;
    Supplier<Pose2d> poseSupplier;

    private Pose2d calculatedPose = new Pose2d();
    public Runnable onDetect = () -> {
    };

    public AprilTagCamera(HardwareMap hardwareMap, Supplier<Pose2d> poseSupplier) {
        this.poseSupplier = poseSupplier;
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(2);
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "AprilCam"))
                .addProcessor(aprilTagProcessor)
                .build();
        CommandScheduler.getInstance().schedule(new CameraCalibrate(this));
    }

    private Pose2d calculateBotPose(AprilTagDetection detection) { // The provided angles are in degrees, and intrinsic
        Pose2d tagPose = Constants.Vision.backdropTagPoses[detection.id - 1];
        Vector2d relativeCoordinates = new Vector2d(detection.ftcPose.x, detection.ftcPose.y).rotate(tagPose.rotation.getAngleRadians());
        Pose2d camPose = new Pose2d(
                tagPose.x - relativeCoordinates.x,
                tagPose.y - relativeCoordinates.y,
                Constants.Vision.useAprilTagHeading && Math.abs(detection.ftcPose.yaw) < Constants.Vision.aprilTagHeadingThresholdDegrees ? Rotation2d.fromDegrees(tagPose.rotation.getAngleDegrees() - detection.ftcPose.yaw) : poseSupplier.get().rotation);
        Vector2d relativeBotCoordinates = Constants.Vision.camToRobot.rotate(camPose.rotation.getAngleRadians());
        return new Pose2d(camPose.x + relativeBotCoordinates.x, camPose.y + relativeBotCoordinates.y, camPose.rotation);
    }

    private void adjustBotPose() {
        Vector2d position = new Vector2d();
        ArrayList<Rotation2d> rotations = new ArrayList<>();
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        int i = 0;
        for (AprilTagDetection detection : detections) {
            DashboardLayout.setNodeValue("follow", detection.id);
            if (detection.metadata != null && detection.ftcPose.range < Constants.Vision.useAprilTagMaxDistIn && detection.id >= 1 && detection.id <= 6) {
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
        exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
    }

    public void enable() {
        cameraEnabled = true;
    }

    public void disable() {
        cameraEnabled = false;
    }

    public Pose2d getCalculatedPose() {
        return calculatedPose;
    }

    @Override
    public void periodic() {
        adjustBotPose();
        Pose2d botPose = poseSupplier.get();
        boolean withinRange = Math.abs(Rotation2d.signed_minusPI_to_PI(botPose.rotation.getAngleRadians())) < Math.toRadians(Constants.Vision.turnCamOnThresholdDegrees) && botPose.x < Constants.Vision.useAprilTagMaxDistIn;
        if (withinRange && cameraEnabled) {
            if (!usingCamera) {
                usingCamera = true;
                visionPortal.resumeStreaming();
            }
            adjustBotPose();
        } else if (usingCamera) {
            usingCamera = false;
            try {
                visionPortal.stopStreaming();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
