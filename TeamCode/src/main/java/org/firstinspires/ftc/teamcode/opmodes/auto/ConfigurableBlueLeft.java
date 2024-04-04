package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.vision.Pipeline;

@Autonomous(name = "test")
public class ConfigurableBlueLeft extends Auton {
    public ConfigurableBlueLeft() {
        super(Pipeline.Alliance.BLUE, Constants.Drive.StartPositions.blueLeft);
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = new FollowPath(Path.loadPath("blue_left_auto"), drive);
    }

    @Override
    public void start() {
        CommandScheduler.getInstance().schedule(autonomousCommand);
    }
}
