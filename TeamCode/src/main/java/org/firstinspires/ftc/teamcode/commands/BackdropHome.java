package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.MecanumBase;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.drive.WaypointGenerator;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

public class BackdropHome extends Command {
    private final MecanumBase base;

    private final Slide slide;
    private final Placer placer;

    private final WaypointGenerator futureBackdropWaypoint;

    private Waypoint backdropWaypoint;

    private final double followTimeout;

    private final double endTime;
    private boolean atWaypoint = false;

    private boolean lastAtWaypoint = false;
    private double timestamp = -1;

    public BackdropHome(MecanumBase base, Slide slide, Placer placer, WaypointGenerator futureBackdropWaypoint, double followTimeout, double endTime) {
        this.futureBackdropWaypoint = futureBackdropWaypoint;
        this.base = base;
        this.slide = slide;
        this.placer = placer;
        this.followTimeout = followTimeout;
        this.endTime = endTime;
    }

    @Override
    public void initialize() {
        atWaypoint = false;
        lastAtWaypoint = false;
        base.driveController.setGains(0.1, 0.00001, 4);
        base.rotController.setGains(5.0, 0.0001, 0.6);
        backdropWaypoint = futureBackdropWaypoint.getWaypoint();
    }

    @Override
    public void execute() {
        base.driveToPosition(backdropWaypoint);
    }

    @Override
    public boolean isFinished() {
        lastAtWaypoint = atWaypoint;
        atWaypoint = base.atWaypoint(backdropWaypoint, 0.5, 5)
                || timeSinceInitialized() > followTimeout
                || placer.touchSensor.isPressed()
                || (placer.getDistance() < 1.0 && slide.encoder.getPosition() > 500);

        if (atWaypoint && !lastAtWaypoint) {
            timestamp = timeSinceInitialized();
        }

        return atWaypoint && timeSinceInitialized() > timestamp + endTime;
    }

    @Override
    public void end(boolean interrupted) {
        base.driveController.setGains(Constants.Drive.defaultDriveGains);
        base.rotController.setGains(Constants.Drive.defaultRotGains);
    }

}
