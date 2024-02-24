package org.firstinspires.ftc.teamcode.commandsystem;

import java.util.ArrayList;

public class SequentialCommandGroup extends Command {
    private final Command[] commands;

    private boolean hasStarted = false;
    private int index = 0;

    public SequentialCommandGroup(Command... commands) {
        this.commands = commands;
    }

    @Override
    public void initialize() {
        hasStarted = false;
        index = 0;
    }

    @Override
    public void execute() {
        if (commands[index].state == State.UNSCHEDULED) {
            if (hasStarted) {
                if (index < commands.length - 1) index++;
                hasStarted = false;
            } else {
                commands[index].schedule();
                hasStarted = true;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return index == commands.length - 1 && commands[index].isFinished();
    }

    public static class Builder {

        ArrayList<Command> commands = new ArrayList<>();

        private Builder() {
        }

        public Builder add(Command command) {
            commands.add(command);
            return this;
        }

        public SequentialCommandGroup build() {
            return new SequentialCommandGroup(commands.toArray(new Command[]{}));
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
