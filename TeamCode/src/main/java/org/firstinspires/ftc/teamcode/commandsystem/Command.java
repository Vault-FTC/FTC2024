package org.firstinspires.ftc.teamcode.commandsystem;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public abstract class Command {
    ArrayList<Trigger> triggers = new ArrayList<>();

    public Command() {
        CommandScheduler.getInstance().commands.add(this);
    }

    enum State {
        UNSCHEDULED,
        QUEUED,
        SCHEDULED,
        ENDING
    }

    enum Type {
        DEFAULT_COMMAND,
        NORMAL
    }

    Type type = Type.NORMAL;

    final boolean triggered() {
        if (type == Type.DEFAULT_COMMAND) return true;
        for (Trigger trigger : triggers) {
            if (trigger.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    protected State state = State.UNSCHEDULED;

    public void initialize() {

    }

    public abstract void execute();

    public void end(boolean interrupted) {

    }

    public boolean isFinished() {
        return true;
    }

    public void schedule() {
        state = State.QUEUED;
    }

    public void cancel() {
        state = State.UNSCHEDULED;
        end(true);
    }

    protected final void addRequirements(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            subsystem.requirements.add(this);
        }
    }
}
