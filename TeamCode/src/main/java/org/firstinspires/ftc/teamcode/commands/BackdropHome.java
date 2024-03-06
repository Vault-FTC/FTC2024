package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.MecanumBase;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.subsystems.Placer;

public class BackdropHome extends Command {
    private final MecanumBase base;

    private final Placer placer;

    private final Waypoint backdropWaypoint;

    private final double followTimeout;

    private final double endTime;
    private boolean atWaypoint = false;

    private int timestamp = -1;

    public BackdropHome(MecanumBase base, Placer placer, Pose2d backdropPose, double followTimeout, double endTime) {
        backdropWaypoint = backdropPose.toWaypoint();
        this.base = base;
        this.placer = placer;
        this.followTimeout = followTimeout;
        this.endTime = endTime;
    }

    @Override
    public void initialize() {
        atWaypoint = false;
        base.driveController.setGains(0.3, 0.0003, 3.5);
        base.rotController.setGains(3.0, 0.0001, 0.6);
    }

    @Override
    public void execute() {
        base.driveToPosition(backdropWaypoint);
    }

    @Override
    public boolean isFinished() {
        atWaypoint = base.atWaypoint(backdropWaypoint, 0.5, 5)
                || timeSinceInitialized() > initializedTimestamp() + followTimeout
                || placer.touchSensor.isPressed()
                || placer.distanceSensor.getDistance(DistanceUnit.INCH) < 0.5;
        if (atWaypoint && timestamp == -1) {
            if (timestamp == -1) {
                timestamp = (int) timeSinceInitialized();
            } else {
                return timeSinceInitialized() - timestamp > endTime;
            }
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        base.driveController.setGains(Constants.Drive.defaultDriveGains);
        base.rotController.setGains(Constants.Drive.defaultRotGains);
    }

}
