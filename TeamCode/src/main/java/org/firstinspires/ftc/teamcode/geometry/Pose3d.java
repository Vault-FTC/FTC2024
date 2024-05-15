package org.firstinspires.ftc.teamcode.geometry;

public class Pose3d extends Vector3d {
    public final Rotation2d pitch;
    public final Rotation2d roll;
    public final Rotation2d yaw;

    public Pose3d(Vector3d vector, Rotation2d pitch, Rotation2d roll, Rotation2d yaw) {
        super(vector.x, vector.y, vector.z);
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }
}
