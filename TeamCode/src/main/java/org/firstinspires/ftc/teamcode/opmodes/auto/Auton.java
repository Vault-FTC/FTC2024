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

    OpenCvWebcam propCam;
    public final int STREAM_WIDTH = 1280;
    public final int STREAM_HEIGHT = 720;

    Pipeline visionPipeline;
    Command autonomousCommand;

    public Auton(Alliance alliance, Rotation2d rotationOffset) {
        Robot.alliance = alliance;
        drive.setFieldCentricOffset(rotationOffset);
    }

    @Override
    public void init() {
        super.init();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        propCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "PropCam"), cameraMonitorViewId);
        visionPipeline = new Pipeline();
        propCam.setPipeline(visionPipeline);
        propCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                propCam.startStreaming(STREAM_WIDTH, STREAM_HEIGHT, OpenCvCameraRotation.UPRIGHT);
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
        propCam.stopStreaming();
        propCam.closeCameraDevice();
    }

    @Override
    public void stop() {
        pose = drive.odometry.getPose();
    }

}
