package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Vector2d;
import org.firstinspires.ftc.teamcode.utils.PIDController.PIDGains;

public class Constants {

    public static final boolean debugMode = true;

    public static final class ControlSettings {
        public static final boolean slideManualControl = true;
    }

    public static final double triggerDeadZone = 0.1;
    public static final double joystickDeadZone = 0.1;

    public static final class Intake {
        public static final double defaultSpeed = 0.8;
    }

    public static final class Slide {
        public static final int maxExtensionPosition = 500;
        public static final int pidDeadband = 10;
        public static final int defaultPlacePosition = 500;
    }

    public static final class Placer {
        public static final double placePosition = 0.4;

        public static final double closePosition = 0;
    }

    public static final class Drive {
        public static final double defaultFollowRadius = 8.0;
        public static final double trackEndpointHeadingMaxDistance = 12.0;
        public static final double calculateTargetHeadingMinDistance = 15.0;

        public static final class StartPositions {
            public static final Pose2d blueLeft = new Pose2d(58.944, 10.019, new Rotation2d());
            public static final Pose2d blueRight = new Pose2d(106.0685, 10.019, new Rotation2d());
            public static final Pose2d redLeft = new Pose2d(106.0685, 131.326, new Rotation2d(Math.PI));
            public static final Pose2d redRight = new Pose2d(58.944, 131.326, new Rotation2d(Math.PI));
        }

        public static final PIDGains defaultDriveGains = new PIDGains(0.2, 0.0, 3.5);
        public static final PIDGains defaultRotGains = new PIDGains(2.0, 0.0001, 0.6);
    }

    public static final class Vision {
        public static final int lookBehindFrames = 30;
        public static final double useAprilTagMaxDistIn = 35;
        public static Vector2d camToRobot = new Vector2d(-5.73, -8.82); // This assumes that the camera's euler angles are always the same as the robot, because I don't want to write a bunch of code for rotation matrices...
        public static final Pose2d[] backdropTagPoses = {
                new Pose2d(9.0, 29.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(9.0, 35.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(9.0, 41.381, new Rotation2d(Math.PI / 2)),
                new Pose2d(9.0, 99.964, new Rotation2d(Math.PI / 2)),
                new Pose2d(9.0, 105.964, new Rotation2d(Math.PI / 2)),
                new Pose2d(9.0, 111.964, new Rotation2d(Math.PI / 2)),
        };
        public static boolean useAprilTagHeading = true;
        public static double aprilTagHeadingThresholdDegrees = 90;
        public static double turnCamOnThresholdDegrees = 15;
    }
}
