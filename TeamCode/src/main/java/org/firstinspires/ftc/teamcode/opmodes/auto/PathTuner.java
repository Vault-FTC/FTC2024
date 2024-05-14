package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.AutonomousCommand;
import org.firstinspires.ftc.teamcode.drive.FollowPathCommand;
import org.firstinspires.ftc.teamcode.drive.Path;

@Disabled
@Autonomous(name = "Path Tuner")
public class PathTuner extends Auton {

    public PathTuner() {
        super(Alliance.RED, Constants.Drive.StartPositions.redRight);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = new AutonomousCommand(new FollowPathCommand(Path.loadPath("red sample path"), drive));
    }

}
