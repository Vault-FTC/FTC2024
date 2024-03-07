package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Slide;

public class SlideCalibrate extends Command {
    private final Slide slide;

    public SlideCalibrate(Slide slide) {
        this.slide = slide;
        addRequirements(slide);
    }

    @Override
    public void execute() {
        slide.motor1.setPower(0.5);
        slide.motor1.setPower(-0.5);
    }

}
