package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.drive.Waypoint;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.utils.GamepadTriggers;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

//@Disabled
@TeleOp(name = "drive to position test")
public class DriveToPositionTest extends OpMode {

    Drive drive;
    Command command;
    GamepadTriggers gamepadTriggers;

    @Override
    public void init() {
        WebdashboardServer.getInstance();

        gamepadTriggers = new GamepadTriggers(gamepad1);

        drive = new Drive(hardwareMap);
        drive.setDefaultCommand(new DriveDefault(drive, () -> -gamepad1.left_stick_y, () -> gamepad1.left_stick_x, () -> -gamepad1.right_stick_x));
        drive.odometry.setPosition(new Pose2d(60, 9));

        command = SequentialCommandGroup.getBuilder()
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(15).setTimeout(3000)
                        .addWaypoint(60, 9)
                        .addWaypoint(60, 30)
                        .build(), drive))
                .add(new WaitCommand(3000))
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(15).setTimeout(10000)
                        .addWaypoint(60, 30)
                        .addWaypoint(new Waypoint(30, 30, 15, Rotation2d.fromDegrees(90), Rotation2d.fromDegrees(90)))
                        .build(), drive))
                .add(new WaitCommand(3000))
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(15)
                        .addWaypoint(new Waypoint(30, 30, 15, Rotation2d.fromDegrees(90), Rotation2d.fromDegrees(90)))
                        .addWaypoint(40, 40)
                        .addWaypoint(50, 50)
                        .addWaypoint(60, 50)
                        .addWaypoint(new Waypoint(100, 50, 15, Rotation2d.fromDegrees(90), Rotation2d.fromDegrees(90)))
                        .build(), drive))
                .build();

        gamepadTriggers.a.onTrue(command);
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().clearRegistry();
    }

}
