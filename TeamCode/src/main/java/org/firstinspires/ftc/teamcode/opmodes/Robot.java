package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;

public abstract class Robot extends OpMode {

    public Drive drive;
    public Intake intake;
    public Slide slide;
    public Placer placer;

    @Override
    public void init() {
        CommandScheduler.getInstance().clearRegistry();
        WebdashboardServer.getInstance(); // Initialize the dashboard server

        // Instantiate subsystems
        drive = new Drive(hardwareMap);
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        slide = new Slide(hardwareMap.get(DcMotor.class, "slideMotor"), hardwareMap.get(TouchSensor.class, "limit"));
        placer = new Placer(hardwareMap);


    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }

}
