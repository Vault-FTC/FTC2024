package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.commandsystem.CommandScheduler;
import org.firstinspires.ftc.teamcode.opmodes.tele.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Placer;
import org.firstinspires.ftc.teamcode.subsystems.Slide;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Auton extends OpMode {

    OpenCvWebcam webcam;

    public final int STREAM_WIDTH = 1280;
    public final int STREAM_HEIGHT = 720;

    Pipeline visionPipeline;
    Drive drive;
    Intake intake;
    Slide slide;
    Placer placer;

    Command autonomousCommand;

    public enum AutonType {
        BLUE_LEFT,
        BLUE_RIGHT,
        RED_LEFT,
        RED_RIGHT
    }

    final AutonType type;

    public Auton(AutonType type) {
        this.type = type;
    }

    @Override
    public void init() {
        CommandScheduler.getInstance().clearRegistry();
        WebdashboardServer.getInstance(); // Initialize the dashboard server
        Robot.robotState = Robot.State.INITIALIZING;
        WebdashboardServer.getInstance();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam1"), cameraMonitorViewId);
        visionPipeline = new Pipeline(type == AutonType.BLUE_RIGHT || type == AutonType.BLUE_LEFT ? Pipeline.Alliance.BLUE : Pipeline.Alliance.RED);
        webcam.setPipeline(visionPipeline);
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
        intake = new Intake(hardwareMap.get(DcMotor.class, "intakeMotor"));
        slide = new Slide(hardwareMap.get(DcMotor.class, "slideMotor"), hardwareMap.get(TouchSensor.class, "limit"));
        placer = new Placer(hardwareMap);


    }

    @Override
    public void init_loop() {
        telemetry.addData("prop location: ", visionPipeline.getPropLocation().toString());
        DashboardLayout.setNodeValue("prop location", visionPipeline.getPropLocation().toString());
    }


    @Override
    public void start() {
        Robot.robotState = Robot.State.AUTO;
        visionPipeline.close();
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        autonomousCommand.schedule();
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
