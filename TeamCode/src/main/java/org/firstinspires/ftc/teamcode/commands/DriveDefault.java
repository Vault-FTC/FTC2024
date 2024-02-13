package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

import java.util.function.DoubleSupplier;

public class DriveDefault extends Command {
    private final DoubleSupplier drive;
    private final DoubleSupplier turn;
    private final DoubleSupplier strafe;
    private final Drive subsystem;

    public DriveDefault(Drive subsystem, DoubleSupplier drive, DoubleSupplier turn, DoubleSupplier strafe) {
        this.subsystem = subsystem;
        this.drive = drive;
        this.turn = turn;
        this.strafe = strafe;
    }

    @Override
    public void execute() {
        subsystem.drive(drive.getAsDouble(), turn.getAsDouble(), strafe.getAsDouble());
    }
}
