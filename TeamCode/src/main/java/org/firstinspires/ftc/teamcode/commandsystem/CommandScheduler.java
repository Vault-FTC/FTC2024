package org.firstinspires.ftc.teamcode.commandsystem;

import java.util.ArrayList;

public final class CommandScheduler implements Runnable {
    private static CommandScheduler instance = null;
    ArrayList<Command> commands;
    ArrayList<Subsystem> subsystems;

    public CommandScheduler() {
        commands = new ArrayList<>();
        subsystems = new ArrayList<>();
    }

    public void schedule(Command command) {
        command.schedule();
    }

    @Override
    public void run() {
        for (Subsystem subsystem : subsystems) {
            subsystem.periodic();
        }
        for (Command command : commands) {
            if (command.triggered() && command.state == Command.State.UNSCHEDULED)
                command.state = Command.State.QUEUED;
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.cancelConflictingCommands();
        }
        for (Command command : commands) {
            switch (command.state) {
                case QUEUED:
                    command.state = Command.State.SCHEDULED;
                    command.initialize();
                    break;
                case SCHEDULED:
                    if (command.isFinished()) command.state = Command.State.ENDING;
                    command.execute();
                    break;
                case ENDING:
                    command.state = Command.State.UNSCHEDULED;
                    command.end(false);
                    break;
            }
        }
    }

    public static CommandScheduler getInstance() {
        if (instance == null) {
            instance = new CommandScheduler();
        }
        return instance;
    }
}
