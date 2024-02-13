package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

import java.util.function.DoubleSupplier;

public class SlideDefault extends Command {

    Slide subsystem;
    DoubleSupplier speed;

    public SlideDefault(Slide slide, DoubleSupplier speed) {
        subsystem = slide;
        this.speed = speed;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        if (Constants.ControlSettings.slideManualControl) subsystem.drive(-speed.getAsDouble());
    }
}
