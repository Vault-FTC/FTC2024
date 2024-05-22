package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.commands.CameraCalibrate;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.Subsystem;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose3d;
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
    private final Pose3d pose;
    private final AprilTag[] tags;
    public final VisionPortal visionPortal;
    public boolean cameraEnabled = false;
    private boolean streaming = false;
    AprilTagProcessor aprilTagProcessor;
    private Pose2d calculatedPose = new Pose2d();
    private final Runnable onDetect;
    private final CameraActivationZone[] activationZones;

    private AprilTagCamera(Builder builder) {
        pose = builder.pose;
        tags = builder.tags;
        onDetect = builder.onDetect;
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(0);
        aprilTagProcessor.setPoseSolver(AprilTagProcessor.PoseSolver.OPENCV_IPPE_SQUARE);
        visionPortal = new VisionPortal.Builder()
                .setCamera(builder.hardwareMap.get(WebcamName.class, "backCam"))
                .addProcessor(aprilTagProcessor)
                .build();
        activationZones = builder.activationZones;
        CommandScheduler.getInstance().schedule(new CameraCalibrate(this));
    }

    public interface SetHardwareMap {
        SetRelativePose setHardwareMap(HardwareMap hardwareMap);
    }

    public interface SetRelativePose {
        AddTags setRelativePose(Pose3d pose);
    }

    public interface AddTags {
        Builder addTags(AprilTag... tags);
    }

    public static class Builder implements SetHardwareMap, SetRelativePose, AddTags {
        private HardwareMap hardwareMap;
        private Pose3d pose;
        private AprilTag[] tags;
        private CameraActivationZone[] activationZones = {};
        private Runnable onDetect = () -> {
        };

        private Builder() {

        }

        @Override
        public SetRelativePose setHardwareMap(HardwareMap hardwareMap) {
            this.hardwareMap = hardwareMap;
            return this;
        }

        @Override
        public AddTags setRelativePose(Pose3d pose) {
            this.pose = pose;
            return this;
        }

        @Override
        public Builder addTags(AprilTag... tags) {
            this.tags = tags;
            return this;
        }

        public Builder setCameraActivationZones(CameraActivationZone... activationZones) {
            this.activationZones = activationZones;
            return this;
        }

        public Builder onDetect(Runnable onDetect) {
            this.onDetect = onDetect;
            return this;
        }

        public AprilTagCamera build() {
            return new AprilTagCamera(this);
        }
    }

    public static SetHardwareMap getBuilder() {
        return new Builder();
    }

    private Pose3d getCameraPose(AprilTagDetection... detections) {
        for (AprilTagDetection detection : detections) {
            // TODO: finish writing this and test it
        }
        return new Pose3d();
    }

    private Pose2d calculateBotPose(AprilTagDetection detection) { // The provided angles are intrinsic and in degrees
        return new Pose2d();
    }

    private void adjustBotPose() {
        Vector2d position = new Vector2d();
        ArrayList<Rotation2d> rotations = new ArrayList<>();
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        int i = 0;
        for (AprilTagDetection detection : detections) {
            RustboardLayout.setNodeValue("follow", detection.id);
            if (detection.metadata != null && AprilTag.getTag(detection.id) != null) {
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
        boolean withinRange = activationZones.length == 0;
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
