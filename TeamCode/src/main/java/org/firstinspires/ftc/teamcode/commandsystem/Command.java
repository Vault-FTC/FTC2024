package org.firstinspires.ftc.teamcode.commandsystem;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public abstract class Command {
    ArrayList<Trigger> triggers = new ArrayList<>();

    ElapsedTime timer = new ElapsedTime();
    double initializedTimestamp = 0;

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
        boolean triggered = false;
        for (Trigger trigger : triggers) { // The getAsBoolean method must be called for every trigger, because it will set the prior state of each trigger.
            if (trigger.getAsBoolean())
                triggered = true;
        }
        return triggered;
    }

    protected State state = State.UNSCHEDULED;

    public void initialize() {

    }

    public double getInitializedTimestamp() {
        return initializedTimestamp;
    }

    public double getTimeSinceInitialized() {
        return timer.milliseconds() - initializedTimestamp;
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

    public final void cancel() {
        state = State.UNSCHEDULED;
        end(true);
    }

    protected final void addRequirements(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            subsystem.requirements.add(this);
        }
    }
    
}
