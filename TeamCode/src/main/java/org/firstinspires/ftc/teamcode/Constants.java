package org.firstinspires.ftc.teamcode;

public class Constants {

    public static final boolean debugMode = true;

    public static final class ControlSettings {
        public static final boolean slideManualControl = false;
    }

    public static final double triggerDeadZone = 0.1;
    public static final double joystickDeadZone = 0.1;
    public static final double deadZone = 0.05;

    public static final class Intake {
        public static final double idleSpeed = 0.3;
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
}
