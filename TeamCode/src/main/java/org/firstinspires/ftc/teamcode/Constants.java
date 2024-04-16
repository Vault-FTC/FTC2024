package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import org.firstinspires.ftc.teamcode.control.PIDController.PIDGains;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;

import java.io.File;

public class Constants {

    public static final File storageDir = new File(Environment.getExternalStorageDirectory() + "/Download");
    public static final boolean debugMode = true;
    public static final double fieldLengthIn = 141.345;

    public static final double triggerDeadZone = 0.1;
    public static final double joystickDeadZone = 0.1;

    public static final class Intake {
        public static final double defaultSpeed = 0.8;
    }

    public static final class Slide {
        public static final int preparePlacerPosition = 350;
        public static final int maxExtensionPosition = 1500;
        public static final int pidDeadband = 50;
        public static final int defaultPlacePosition = 600;
        public static final double maxDownSpeed = 0.6;
        public static final int stowedPosition = -100;
    }

    public static final class Placer {
        public static final double lifter0PlacePosition = 0.1;
        public static final double lifter1PlacePosition = 0.7;
        public static final double lifter0StoragePosition = 0.3;
        public static final double lifter1StoragePosition = 0.5;
        public static final double openPosition = 0.56;
        public static final double closePosition = 0.62;
    }

    public static final class Drive {
        public static final double defaultFollowRadius = 15;
        public static final double trackEndpointHeadingMaxDistance = 12.0;
        public static final double calculateTargetHeadingMinDistance = 15.0;

        public static final class StartPositions {
            public static final Pose2d blueLeft = new Pose2d(58.944, 7.916899, new Rotation2d(Math.PI));
            public static final Pose2d blueRight = new Pose2d(106.0685, 7.916899, new Rotation2d());
            public static final Pose2d redLeft = new Pose2d(106.0685, fieldLengthIn - 7.916899, new Rotation2d(Math.PI));
            public static final Pose2d redRight = new Pose2d(58.944, fieldLengthIn - 7.916899, new Rotation2d(Math.PI));
        }

        public static final PIDGains defaultDriveGains = new PIDGains(0.05, 0.0, 1.5);
        public static final PIDGains defaultRotGains = new PIDGains(0.075, 0.00001, 0.6);
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
