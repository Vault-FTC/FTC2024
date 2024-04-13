package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;

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
        double speed = this.speed.getAsDouble();
        if (Math.abs(speed) > 0) {
            subsystem.drive(speed);
            DashboardLayout.setNodeValue("slide target", subsystem.getTargetPosition());
        }
    }

}
