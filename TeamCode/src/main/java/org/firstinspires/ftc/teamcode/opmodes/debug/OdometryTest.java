package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.Odometry;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

@Disabled
@TeleOp(name = "odo test")
public class OdometryTest extends OpMode {

    Odometry odometry;

    @Override
    public void init() {
        WebdashboardServer.getInstance();
        odometry = new Odometry(hardwareMap);
    }

    @Override
    public void loop() {
        odometry.update();
        DashboardLayout.setNodeValue("pose", odometry.getPose());
    }

}
