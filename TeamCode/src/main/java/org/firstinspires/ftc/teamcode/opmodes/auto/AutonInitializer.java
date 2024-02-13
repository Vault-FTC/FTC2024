package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.opmodes.tele.Robot;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.firstinspires.ftc.teamcode.webdashboard.WebdashboardServer;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name="Autonomous")

public class AutonInitializer extends OpMode {

    OpenCvWebcam webcam;

    public final int STREAM_WIDTH = 1280;
    public final int STREAM_HEIGHT = 720;

    Pipeline pipeline;


    @Override
    public void init() {
        Robot.robotState = Robot.State.INITIALIZING;
        WebdashboardServer.getInstance();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam1"), cameraMonitorViewId);
        pipeline = new Pipeline();
        webcam.setPipeline(pipeline);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(STREAM_WIDTH, STREAM_HEIGHT, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Failed","");
                telemetry.update();
            }
        });
    }


    @Override
    public void start() {
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        Robot.robotState = Robot.State.AUTO;
    }


    @Override
    public void loop() {
        telemetry.addData("prop location: ", pipeline.getPropLocation().toString());
        DashboardLayout.setNodeValue("prop location", pipeline.getPropLocation().toString());
    }


    @Override
    public void stop() {
        Robot.robotState = Robot.State.DISABLED;
    }
}
