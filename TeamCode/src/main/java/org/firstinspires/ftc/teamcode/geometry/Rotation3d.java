package org.firstinspires.ftc.teamcode.geometry;

public class Rotation3d {
    public final Rotation2d pitch;
    public final Rotation2d roll;
    public final Rotation2d yaw;

    public Rotation3d(Rotation2d pitch, Rotation2d roll, Rotation2d yaw) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }

    public Rotation3d() {
        this(new Rotation2d(), new Rotation2d(), new Rotation2d());
    }
}
