package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

public class FollowFuturePath extends Command {
    private final Supplier<Path> pathSupplier;
    private final Drive subsystem;

    Gamepad gamepad;

    public FollowFuturePath(Supplier<Path> pathSupplier, Drive drive, Gamepad gamepad) {
        this.pathSupplier = pathSupplier;
        subsystem = drive;
        this.gamepad = gamepad;
    }

    public FollowFuturePath(Supplier<Path> pathSupplier, Drive drive) {
        this(pathSupplier, drive, null);
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        subsystem.base.setFollowPath(pathSupplier.get());
    }

    @Override
    public void execute() {
        DashboardLayout layout = Server.getInstance().getLayout("dashboard_0");
        subsystem.base.followPath();
        if (gamepad != null && timeSinceInitialized() > 250 && !gamepad.atRest()) {
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
