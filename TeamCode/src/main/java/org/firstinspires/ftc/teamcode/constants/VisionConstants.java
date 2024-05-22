package org.firstinspires.ftc.teamcode.constants;

import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose3d;
import org.firstinspires.ftc.teamcode.subsystems.AprilTag;

public class VisionConstants {
    public static final int elementDetectionLookBehindFrames = 30;
    public static final AprilTag[] aprilTags = {
            new AprilTag(new Pose3d(), 1),
            new AprilTag(new Pose3d(), 2),
            new AprilTag(new Pose3d(), 3),
            new AprilTag(new Pose3d(), 4),
            new AprilTag(new Pose3d(), 5),
            new AprilTag(new Pose3d(), 6),
    };
}
