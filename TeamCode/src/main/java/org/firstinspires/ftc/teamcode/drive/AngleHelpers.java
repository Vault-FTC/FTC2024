package org.firstinspires.ftc.teamcode.drive;

public class AngleHelpers {
    public static double minimumMagnitude(double... values) {
        double min = Double.POSITIVE_INFINITY;
        for (double value : values) {
            if (Math.abs(value) < Math.abs(min)) min = value;
        }
        return min;
    }

    public static double getError(double targetAngle, double currentAngle) {
        targetAngle = unsigned_0_to_2PI(targetAngle);
        currentAngle = unsigned_0_to_2PI(currentAngle);
        return minimumMagnitude(targetAngle - currentAngle, targetAngle + 2 * Math.PI - currentAngle, targetAngle - 2 * Math.PI - currentAngle);
    }

    public static double unsigned_0_to_2PI(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    public static double unsigned_negativePI_to_PI(double angle) {
        angle = angle % (2 * Math.PI);
        if (Math.abs(angle) > Math.PI) {
            angle += -2 * Math.PI * angle / Math.abs(angle);
        }
        return angle;
    }

    public static double unsigned_0_to_360(double angle) {
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    public static double unsigned_negative180_to_180(double angle) {
        angle = angle % 360;
        if (Math.abs(angle) > 180) {
            angle += -360 * angle / Math.abs(angle);
        }
        return angle;
    }
}
