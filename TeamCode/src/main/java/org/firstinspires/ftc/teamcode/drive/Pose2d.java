package org.firstinspires.ftc.teamcode.drive;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.Constants;

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
        return toWaypoint(Constants.Drive.defaultFollowRadius);
    }

    @NonNull
    @Override
    public String toString() {
        return "x:" + x + "y:" + y + "heading:" + rotation.getAngleRadians();
    }

}
