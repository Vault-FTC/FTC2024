package org.firstinspires.ftc.teamcode.drive;

public class Waypoint extends Vector2d {

    Vector2d vector;
    double followRadius;

    Rotation2d targetFollowRotation;

    Rotation2d targetEndRotation;

    public Waypoint(Vector2d vector, double followRadius, Rotation2d targetFollowRotation, Rotation2d targetEndRotation) {
        super(vector.x, vector.y);
        this.targetFollowRotation = targetFollowRotation;
        this.targetEndRotation = targetEndRotation;
        this.followRadius = followRadius;
    }

    public Waypoint(Vector2d vector, double followRadius) {
        this(vector, followRadius, null, null);
    }

    public Waypoint(double x, double y, double followRadius, Rotation2d targetFollowRotation, Rotation2d targetEndRotation) {
        this(new Vector2d(x, y), followRadius, targetFollowRotation, targetEndRotation);
    }

    public Waypoint(double x, double y, double followRadius) {
        this(x, y, followRadius, null, null);
    }
}
