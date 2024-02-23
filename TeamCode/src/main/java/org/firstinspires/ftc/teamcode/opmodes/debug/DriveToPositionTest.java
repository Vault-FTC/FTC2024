package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.DriveDefault;
import org.firstinspires.ftc.teamcode.commands.FollowPath;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.WaitCommand;
import org.firstinspires.ftc.teamcode.drive.Path;
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

        command = SequentialCommandGroup.getBuilder()
                .add(new FollowPath(Path.getBuilder().setDefaultRadius(10).setTimeout(7000)
                        .addWaypoint(0, 0)
                        .addWaypoint(0, 26).build(), drive))
                //.add(new WaitCommand(1000))
                /*.add(new FollowPath(Path.getBuilder().setDefaultRadius(10).setTimeout(20000)
                        .addWaypoint(0, 22)
                        .addWaypoint(new Waypoint(-36, 22, 8, Rotation2d.fromDegrees(90), Rotation2d.fromDegrees(90)))
                        .addWaypoint(0, 10)
                        .addWaypoint(10, 25)
                        .addWaypoint(20, 45)
                        //.addWaypoint(30, 60)
                        //.addWaypoint(40, 50)
                        //.addWaypoint(50, 50)
                        //.addWaypoint(new Waypoint(60, 50, Constants.Drive.defaultFollowRadius, null, Rotation2d.fromDegrees(-90)))
                        .build(), drive))*/
                .build();
        command = new FollowPath(Path.getBuilder().setDefaultRadius(10).setTimeout(8000)
                .addWaypoint(0, 0)
                .addWaypoint(0, 26)
                .addWaypoint(10, 40)
                .addWaypoint(20, 45)
                .addWaypoint(30, 45)
                .addWaypoint(40, 40)
                .addWaypoint(50, 30)
                .addWaypoint(60, 20)
                .build(), drive);

        gamepadTriggers.a.onTrue(command);
    }

    public void start() {
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
