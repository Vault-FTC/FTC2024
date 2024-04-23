package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.vision.Pipeline;

@Disabled
@Autonomous(name = "Path Tuner")
public class PathTuner extends Auton {

    public PathTuner() {
        super(Pipeline.Alliance.RED, Constants.Drive.StartPositions.redRight);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = new FollowPath(Path.loadPath("red sample path"), drive);
    }

}
