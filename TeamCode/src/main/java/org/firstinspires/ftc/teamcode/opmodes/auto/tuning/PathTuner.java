package org.firstinspires.ftc.teamcode.opmodes.auto.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.opmodes.auto.AutonContainer;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.AutonomousCommand;
import org.firstinspires.ftc.teamcode.org.rustlib.drive.FollowPathCommand;
import org.firstinspires.ftc.teamcode.org.rustlib.drive.Path;

@Disabled
@Autonomous(name = "Path Tuner")
public class PathTuner extends AutonContainer {

    public PathTuner() {
        super(Alliance.RED, redRightStartPosition);
    }

    @Override
    public void setup() {
        autonomousCommand = new AutonomousCommand(new FollowPathCommand(Path.loadPath("red sample path"), drive));
    }
}
