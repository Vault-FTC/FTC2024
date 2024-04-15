package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

import java.util.function.DoubleSupplier;

public class SlideDefault extends Command {

    Slide subsystem;
    DoubleSupplier speed;

    private double lastSpeed = 0;

    public SlideDefault(Slide slide, DoubleSupplier speed) {
        subsystem = slide;
        this.speed = speed;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        double speed = this.speed.getAsDouble();
        if (speed == 0 && lastSpeed != 0) {
            subsystem.setTargetPosition(subsystem.encoder.getPosition());
        }
        lastSpeed = speed;
        if (Math.abs(speed) > 0) {
            subsystem.drive(speed);
        } else {
            subsystem.driveToPosition();
        }
    }

}
