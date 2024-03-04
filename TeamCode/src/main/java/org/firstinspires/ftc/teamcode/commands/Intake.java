package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;

public class Intake extends Command {

    private final org.firstinspires.ftc.teamcode.subsystems.Intake subsystem;
    private final double speed;

    public Intake(org.firstinspires.ftc.teamcode.subsystems.Intake subsystem, double speed) {
        this.subsystem = subsystem;
        this.speed = speed;
    }

    @Override
    public void execute() {
        subsystem.run(speed);
    }
}
