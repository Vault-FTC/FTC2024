package org.firstinspires.ftc.teamcode.drive;

public class Waypoint extends Vector2d implements WaypointGenerator {
    double followRadius;

    Rotation2d targetFollowRotation;

    Rotation2d targetEndRotation;

    double maxVelocity;

    public Waypoint(Vector2d vector, double followRadius, Rotation2d targetFollowRotation, Rotation2d targetEndRotation, double maxVelocity) {
        super(vector.x, vector.y);
        this.targetFollowRotation = targetFollowRotation;
        this.targetEndRotation = targetEndRotation;
        this.followRadius = followRadius;
        this.maxVelocity = Math.abs(maxVelocity);
    }

    public Waypoint(Vector2d vector, double followRadius) {
        this(vector, followRadius, null, null, Double.POSITIVE_INFINITY);
    }

    public Waypoint(double x, double y, double followRadius, Rotation2d targetFollowRotation, Rotation2d targetEndRotation, double maxVelocity) {
        this(new Vector2d(x, y), followRadius, targetFollowRotation, targetEndRotation, maxVelocity);
    }

    public Waypoint(double x, double y, double followRadius, Rotation2d targetFollowRotation, Rotation2d targetEndRotation) {
        this(x, y, followRadius, targetFollowRotation, targetEndRotation, Double.POSITIVE_INFINITY);
    }

    public Waypoint(double x, double y, double followRadius, double maxVelocity) {
        this(x, y, followRadius, null, null, maxVelocity);
    }

    public Waypoint(double x, double y, double followRadius) {
        this(x, y, followRadius, null, null, Double.POSITIVE_INFINITY);
    }

    @Override
    public Waypoint getWaypoint() {
        return this;
    }

}
