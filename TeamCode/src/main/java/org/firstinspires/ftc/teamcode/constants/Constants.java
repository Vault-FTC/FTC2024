package org.firstinspires.ftc.teamcode.constants;

import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Rotation2d;

public class Constants {
    public static final boolean debugMode = false;
    public static final double fieldLengthIn = 141.345;

    public static final class Intake {
        public static final double defaultSpeed = 0.8;
    }

    public static final class Slide {
        public static final int preparePlacerPosition = 350;
        public static final int stowPlacerPosition = 690;
        public static final int maxExtensionPosition = 1000;
        public static final int maxTargetError = 50;
        public static final int defaultPlacePosition = 760;
        public static final int stowedPosition = -100;

        public static final int autoPlacePosition = 550;
    }

    public static final class Placer {
        public static final double lifter0PlacePosition = 0.1;
        public static final double lifter1PlacePosition = 0.7;
        public static final double lifter0StoragePosition = 0.3;
        public static final double lifter1StoragePosition = 0.5;
        public static final double openPosition = 0.56;
        public static final double closePosition = 0.62;
    }

    public static final class Vision {
        public static final int lookBehindFrames = 30;
        public static final double useAprilTagMaxXIn = 50;
        public static final double useAprilTagMaxRangeIn = 35;
        public static Pose2d camToRobot = new Pose2d(-5.669, -8.713, new Rotation2d(Math.PI)); // This assumes that the camera's euler angles are always the same as the robot, because I don't want to write a bunch of code for rotation matrices...
        public static final Pose2d[] backdropTagPoses = {
                new Pose2d(10.5, 29.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(10.5, 35.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(10.5, 41.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(10.5, 99.964, new Rotation2d(Math.PI / 2)),
                new Pose2d(10.5, 105.964, new Rotation2d(Math.PI / 2)),
                new Pose2d(10.5, 111.964, new Rotation2d(Math.PI / 2)),
        };
        public static boolean useAprilTagHeading = true;
        public static double aprilTagHeadingThresholdDegrees = 10;
        public static double turnCamOnThresholdDegrees = 30;
    }
}
