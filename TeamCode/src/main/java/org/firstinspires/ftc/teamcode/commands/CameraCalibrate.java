package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagCamera;
import org.firstinspires.ftc.vision.VisionPortal;

public class CameraCalibrate extends Command {
    private final AprilTagCamera subsystem;

    public CameraCalibrate(AprilTagCamera subsystem) {
        this.subsystem = subsystem;
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean isFinished() {
        return subsystem.visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING;
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.setExposure(6, 250);
    }
}
