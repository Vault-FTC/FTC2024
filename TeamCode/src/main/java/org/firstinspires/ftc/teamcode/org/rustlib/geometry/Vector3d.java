package org.firstinspires.ftc.teamcode.org.rustlib.geometry;

public class Vector3d {
    public final double x;
    public final double y;
    public final double z;
    public final double magnitude;
    public final double polar; // See https://en.wikipedia.org/wiki/Spherical_coordinate_system
    public final double azimuthal;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        magnitude = calculateRadius();
        polar = Vector2d.calculateAngle(Vector2d.calculateRadius(x, y), z);
        azimuthal = Vector2d.calculateAngle(x, y);
    }

    public Vector3d() {
        this(0, 0, 0);
    }

    private double calculateRadius() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vector3d rotate(double angleRadians, Axis axis) {
        double[] rotatedPoint;
        switch (axis) {
            case X:
                rotatedPoint = Vector2d.rotate(y, z, angleRadians);
                return new Vector3d(x, rotatedPoint[0], rotatedPoint[1]);
            case Y:
                rotatedPoint = Vector2d.rotate(x, z, angleRadians);
                return new Vector3d(rotatedPoint[0], y, rotatedPoint[1]);
            case Z:
                rotatedPoint = Vector2d.rotate(x, y, angleRadians);
                return new Vector3d(rotatedPoint[0], rotatedPoint[1], z);
        }
        throw new RuntimeException("Could not rotate vector");
    }

    public enum Axis {
        X,
        Y,
        Z
    }
}
