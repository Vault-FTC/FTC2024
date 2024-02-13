package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.commandsystem.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.opmodes.tele.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonLoader.AutonType;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Disabled
@Autonomous(name = "Autonomous")

public class Auton extends OpMode {

    OpenCvWebcam webcam;

    public final int STREAM_WIDTH = 1280;
    public final int STREAM_HEIGHT = 720;

    Pipeline pipeline;

    Drive drive;

    SequentialCommandGroup blueLeft;
    SequentialCommandGroup blueRight;
    SequentialCommandGroup redLeft;
    SequentialCommandGroup redRight;

    final AutonType type;

    public Auton(AutonType type, HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.type = type;
    }

    @Override
    public void init() {
        WebdashboardServer.getInstance(); // Initialize the dashboard server
        Robot.robotState = Robot.State.INITIALIZING;
        WebdashboardServer.getInstance();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam1"), cameraMonitorViewId);
        pipeline = new Pipeline(type == AutonType.BLUE_RIGHT || type == AutonType.BLUE_LEFT ? Pipeline.Alliance.BLUE : Pipeline.Alliance.RED);
        webcam.setPipeline(pipeline);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(STREAM_WIDTH, STREAM_HEIGHT, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Failed", "");
                telemetry.update();
            }
        });

        // Instantiate subsystems
        drive = new Drive(hardwareMap);
    }

    @Override
    public void init_loop() {
        telemetry.addData("prop location: ", pipeline.getPropLocation().toString());
        DashboardLayout.setNodeValue("prop location", pipeline.getPropLocation().toString());
    }


    @Override
    public void start() {
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        Robot.robotState = Robot.State.AUTO;
    }


    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }


    @Override
    public void stop() {
        Robot.robotState = Robot.State.DISABLED;
    }
}
