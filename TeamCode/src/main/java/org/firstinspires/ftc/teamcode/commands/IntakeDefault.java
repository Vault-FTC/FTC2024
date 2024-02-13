package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

public class IntakeDefault extends Command {

    private final Intake subsystem;
    private final double speed;

    public IntakeDefault(Intake subsystem, double speed) {
        this.subsystem = subsystem;
        this.speed = speed;
    }

    @Override
    public void execute() {
        subsystem.run(speed);
    }
}
