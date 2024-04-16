package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.control.PIDController;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

public class FollowPath extends Command {

    private final Path path;
    private final Drive subsystem;

    Gamepad gamepad;

    public FollowPath(Path path, Drive drive, Gamepad gamepad) {
        this.path = path;
        subsystem = drive;
        this.gamepad = gamepad;
    }

    public FollowPath(Path path, Drive drive) {
        this(path, drive, null);
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        subsystem.base.setFollowPath(path);
    }

    @Override
    public void execute() {
        DashboardLayout layout = Server.getInstance().getLayout("dashboard_0");
        subsystem.base.driveController.setGains(new PIDController.PIDGains(
                layout.getDoubleValue("drive kP", 0.15),
                layout.getDoubleValue("drive kI", 0.0),
                layout.getDoubleValue("drive kD", 0.5)));
        subsystem.base.rotController.setGains(new PIDController.PIDGains(
                layout.getDoubleValue("rot kP", 0.075),
                layout.getDoubleValue("rot kI", 0.00001),
                layout.getDoubleValue("rot kD", 0.6)));

        subsystem.base.followPath();
        if (gamepad != null && timeSinceInitialized() > 250 && gamepad != null && !gamepad.atRest()) {
            cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return subsystem.base.finishedFollowing();
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.base.setToBrakeMode();
        if (!interrupted) subsystem.drive(0, 0, 0);
    }
}
