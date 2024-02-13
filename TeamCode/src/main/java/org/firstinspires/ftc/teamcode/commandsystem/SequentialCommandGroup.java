package org.firstinspires.ftc.teamcode.commandsystem;

public class SequentialCommandGroup extends Command {
    private final Command[] commands;

    private boolean hasStarted = false;
    private int index = 0;

    public SequentialCommandGroup(Command... commands) {
        this.commands = commands;
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
        return index == commands.length - 1;
    }
}
