package org.firstinspires.ftc.teamcode.org.rustlib.geometry;

public class Pose3d extends Vector3d {
    public final Rotation3d rotation;

    public Pose3d(Vector3d vector, Rotation3d rotation) {
        super(vector.x, vector.y, vector.z);
        this.rotation = rotation;
    }

    public Pose3d(Vector3d vector, Rotation2d pitch, Rotation2d roll, Rotation2d yaw) {
        this(vector, new Rotation3d(pitch, roll, yaw));
    }

    public Pose3d(double x, double y, double z, Rotation2d pitch, Rotation2d roll, Rotation2d yaw) {
        this(new Vector3d(x, y, z), new Rotation3d(pitch, roll, yaw));
    }

    public Pose3d(Vector3d vector) {
        this(vector, new Rotation3d());
    }

    public Pose3d(double x, double y, double z) {
        this(new Vector3d(x, y, z));
    }

    public Pose3d(Rotation3d rotation) {
        this(new Vector3d(), rotation);
    }

    public Pose3d() {
        this(new Vector3d(), new Rotation3d());
    }
}
