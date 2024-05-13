package org.firstinspires.ftc.teamcode.drive;

import androidx.annotation.NonNull;

public class Pose2d extends Vector2d {
    public final Rotation2d rotation;

    public Pose2d(Vector2d vector, Rotation2d rotation) {
        super(vector.x, vector.y);
        this.rotation = rotation;
    }

    public Pose2d(Vector2d vector) {
        this(vector, new Rotation2d());
    }

    public Pose2d(double x, double y, Rotation2d rotation) {
        this(new Vector2d(x, y), rotation);
    }

    public Pose2d(double x, double y) {
        this(x, y, new Rotation2d());
    }

    public Pose2d() {
        this(new Vector2d(), new Rotation2d());
    }

    public Pose2d add(Pose2d toAdd) {
        return new Pose2d(x + toAdd.x, y + toAdd.y, new Rotation2d(rotation.getAngleRadians() + toAdd.rotation.getAngleRadians()));
    }

    public Pose2d multiply(double factor) {
        return new Pose2d(x * factor, y * factor, new Rotation2d(rotation.getAngleRadians() * factor));
    }

    public Pose2d rotate(double angle) {
        return new Pose2d(super.rotate(angle), rotation);
    }

    public Waypoint toWaypoint(double followRadius) {
        return new Waypoint(x, y, followRadius, null, rotation);
    }

    public Waypoint toWaypoint() {
        return toWaypoint(DriveConstants.defaultFollowRadius);
    }

    public Pose2d translateX(double x) {
        return translate(x, 0);
    }

    public Pose2d translateY(double y) {
        return translate(0, y);
    }

    public Pose2d translate(double x, double y) {
        return new Pose2d(x + x, y + y, rotation);
    }

    public Pose2d rotate(Rotation2d rotation) {
        return new Pose2d(x, y, rotation.add(rotation));
    }

    public Pose2d mirror() {
        return new Pose2d(x, DriveConstants.fieldLengthIn - y, rotation.negate().addRadians(Math.PI));
    }

    @NonNull
    @Override
    public String toString() {
        return "x:" + x + "y:" + y + "heading:" + rotation.getAngleRadians();
    }

}
