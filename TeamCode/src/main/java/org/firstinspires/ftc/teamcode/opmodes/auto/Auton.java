package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Rotation2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Auton extends Robot {

    OpenCvWebcam webcam;
    public final int STREAM_WIDTH = 1280;
    public final int STREAM_HEIGHT = 720;

    Pipeline visionPipeline;
    Command autonomousCommand;

    Alliance alliance;

    public Auton(Alliance alliance) {
        this.alliance = alliance;
        if (this.alliance == Alliance.RED) {
            drive.setFieldCentricOffset(new Rotation2d(Math.PI));
        }
    }

    @Override
    public void init() {
        super.init();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam1"), cameraMonitorViewId);
        visionPipeline = new Pipeline(alliance);
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
    }

    @Override
    public void init_loop() {
        telemetry.addData("prop location: ", visionPipeline.getPropLocation().toString());
        DashboardLayout.setNodeValue("prop location", visionPipeline.getPropLocation().toString());
    }


    @Override
    public void start() {
        visionPipeline.close();
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        autonomousCommand.schedule();
    }

    @Override
    public void stop() {
        pose = drive.odometry.getPose();
    }

}
