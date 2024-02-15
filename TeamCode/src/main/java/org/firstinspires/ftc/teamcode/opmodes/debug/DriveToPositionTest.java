package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.drive.Odometry;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

//@Disabled
@TeleOp(name = "drive to position test")
public class DriveToPositionTest extends OpMode {

    Drive drive;

    private boolean start = false;

    @Override
    public void init() {
        WebdashboardServer.getInstance();
        drive = new Drive(hardwareMap);
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        if (gamepad1.a) {
            start = !start;
        }
        if (start) {
            drive.base.driveToPosition(new Waypoint(0, 15, 0, new Rotation2d(), null), false);
        } else {
            drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);
        }
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().clearRegistry();
    }

}
