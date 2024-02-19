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
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static double unsigned_0_to_360(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public static double getAngleDifferenceRadians(double angle1, double angle2) {
        return (angle1 - angle2) % (2 * Math.PI);
    }

    public static double getAngleDifferenceRadians(Rotation2d rotation1, Rotation2d rotation2) {
        return getAngleDifferenceRadians(rotation1.angle, rotation2.angle);
    }

}
