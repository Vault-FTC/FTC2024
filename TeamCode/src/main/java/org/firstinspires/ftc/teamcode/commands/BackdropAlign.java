package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

public class BackdropAlign extends Command {
    private final Drive drive;
    private final Placer placer;
    private double backdropOffset = 0;
    private final double timeout;

    public BackdropAlign(Drive drive, Placer placer, double timeout) {
        this.drive = drive;
        this.placer = placer;
        this.timeout = timeout;
    }

    @Override
    public void execute() {
        Pose2d botPose = drive.odometry.getPose();
        backdropOffset = Server.getInstance().getLayout("dashboard_0").getDoubleValue("place offset", 4.0);
        drive.base.driveToPosition(new Waypoint(botPose.x + placer.getDistance() - backdropOffset, botPose.y, 0, Rotation2d.fromDegrees(-90), Rotation2d.fromDegrees(-90), 0.6));
    }

    @Override
    public boolean isFinished() {
        return Math.abs(Rotation2d.unsigned_0_to_2PI(drive.odometry.getPose().rotation.getAngleRadians()) - Math.toRadians(270)) < Math.toRadians(5.0)
                && Math.abs(placer.getDistance() - backdropOffset) < 0.5
                || timeSinceInitialized() > timeout;
    }
}
