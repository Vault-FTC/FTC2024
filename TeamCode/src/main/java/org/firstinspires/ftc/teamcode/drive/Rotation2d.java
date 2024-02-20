package org.firstinspires.ftc.teamcode.drive;

public class Rotation2d {

    /**
     * Constructs a new rotation2d with the provided radian value
     **/
    private final double angle;

    public Rotation2d(double angle) {
        this.angle = angle;
    }

    public Rotation2d() {
        this(0);
    }

    public static Rotation2d fromDegrees(double angleDegrees) {
        return new Rotation2d(angleDegrees / 180 * Math.PI);
    }

    public double getAngleRadians() {
        return angle;
    }

    public double getAngleDegrees() {
        return angle * 180 / Math.PI;
    }

    public static double unsigned_0_to_2PI(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public static double unsigned_0_to_360(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

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

}
