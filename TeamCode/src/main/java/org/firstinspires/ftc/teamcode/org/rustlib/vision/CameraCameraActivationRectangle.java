package org.firstinspires.ftc.teamcode.org.rustlib.vision;

import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Vector2d;

import java.util.function.Supplier;

public class CameraCameraActivationRectangle implements CameraActivationZone {
    private final Vector2d center;
    private final double width;
    private final double height;
    private final Rotation2d headingMin;
    private final Rotation2d headingMax;
    private final Supplier<Pose2d> poseSupplier;

    public CameraCameraActivationRectangle(Vector2d center, double width, double height, Rotation2d headingMin, Rotation2d headingMax, Supplier<Pose2d> poseSupplier) {
        this.center = center;
        this.width = width;
        this.height = height;
        this.headingMin = headingMin;
        this.headingMax = headingMax;
        this.poseSupplier = poseSupplier;
    }

    @Override
    public boolean withinZone() {
        Pose2d botPose = poseSupplier.get();
        double halfWidth = width / 2;
        double halfHeight = height / 2;
        return botPose.x > center.x - halfWidth && botPose.x < center.x + halfWidth && botPose.y > center.y - halfHeight && botPose.y < center.y + halfHeight;
    }
}
