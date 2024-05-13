package org.firstinspires.ftc.teamcode.drive;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.CommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;

import java.util.ArrayList;
import java.util.function.Supplier;

public class AutonomousCommand extends Command {

    private final Command command;
    private boolean timerEnabled = true;

    public AutonomousCommand(Command command) {
        this.command = command;
    }

    @Override
    public void initialize() {
        command.schedule();
    }

    @Override
    public void execute() {
        if (timeSinceInitialized() > 30000 && timerEnabled) {
            cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return command.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            command.cancel();
        }
    }

    public void disableTimer() {
        timerEnabled = false;
    }

    public AutonomousCommand mirrorPaths() {
        ArrayList<Command> commands = new ArrayList<>();
        if (command instanceof CommandGroup) {
            for (Command command : ((CommandGroup) command).getCommands()) {
                command = mirrorContainedPath(command);
                commands.add(command);
            }
            if (command instanceof SequentialCommandGroup) {
                return new AutonomousCommand(new SequentialCommandGroup(commands.toArray(new Command[]{})));
            } else {
                return new AutonomousCommand(new ParallelCommandGroup(commands.toArray(new Command[]{})));
            }
        } else {
            return new AutonomousCommand(mirrorContainedPath(command));
        }
    }

    private Command mirrorContainedPath(Command command) {
        if (command instanceof FollowPathCommand) {
            FollowPathCommand pathCommand = (FollowPathCommand) command;
            Supplier<Path> path = () -> pathCommand.pathSupplier.get().mirror();
            command = new FollowPathCommand(path, pathCommand.driveSubsystem);
        }
        return command;
    }
}
