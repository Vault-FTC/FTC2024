package org.firstinspires.ftc.teamcode.commandsystem;

import java.util.ArrayList;

public class ParallelCommandGroup extends Command {
    private final Command[] commands;

    public ParallelCommandGroup(Command... commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        for (Command command : commands) {
            if (!command.isFinished()) {
                return false;
            }
        }
        return true;
    }

    public static class Builder {

        ArrayList<Command> commands = new ArrayList<>();

        private Builder() {
        }

        public Builder add(Command command) {
            commands.add(command);
            return this;
        }

        public ParallelCommandGroup build() {
            return new ParallelCommandGroup(commands.toArray(new Command[]{}));
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
