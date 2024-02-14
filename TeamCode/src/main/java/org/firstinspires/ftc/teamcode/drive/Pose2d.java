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

    @NonNull
    @Override
    public String toString() {
        return "x:" + x + "y:" + y + "heading:" + rotation.getAngleRadians();
    }

}
