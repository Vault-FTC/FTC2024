package org.firstinspires.ftc.teamcode.commandsystem;

import org.firstinspires.ftc.teamcode.drive.FollowPathCommand;
import org.firstinspires.ftc.teamcode.drive.Path;

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
        return new AutonomousCommand(recursiveMirror(command));
    }

    private Command recursiveMirror(Command toMirror) {
        ArrayList<Command> commands = new ArrayList<>();
        if (toMirror instanceof CommandGroup) {
            for (Command command : ((CommandGroup) toMirror).getCommands()) {
                command = recursiveMirror(command);
                commands.add(command);
            }
            if (toMirror instanceof SequentialCommandGroup) {
                return new SequentialCommandGroup(commands.toArray(new Command[]{}));
            } else {
                return new ParallelCommandGroup(commands.toArray(new Command[]{}));
            }
        } else {
            return mirrorContainedPath(toMirror);
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
