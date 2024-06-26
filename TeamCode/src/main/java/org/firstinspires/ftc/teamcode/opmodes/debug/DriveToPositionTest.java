package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

@TeleOp(name = "drive to position test")
public class DriveToPositionTest extends Robot {
    @Override
    public void loop() {
        if (Server.getInstance().getLayout("dashboard_0").getBooleanValue("target")) {
            drive.base.driveToPosition(new Pose2d(25, 25).toWaypoint());
        } else {
            drive.base.driveToPosition(new Pose2d(0, 0).toWaypoint());
        }
        CommandScheduler.getInstance().run();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().clearRegistry();
    }

}
