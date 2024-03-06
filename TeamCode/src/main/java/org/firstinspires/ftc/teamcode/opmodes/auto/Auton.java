package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Auton extends Robot {

    OpenCvWebcam propCam;
    Pipeline visionPipeline;
    Command autonomousCommand;

    private final Pose2d startPosition;

    public Auton(Alliance alliance, Pose2d startPosition) {
        Robot.alliance = alliance;
        this.startPosition = startPosition;
    }

    @Override
    public void init() {
        super.init();
        drive.odometry.setPosition(startPosition);
        drive.setFieldCentricOffset(startPosition.rotation);

        propCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "PropCam"));
        visionPipeline = new Pipeline();
        propCam.setPipeline(visionPipeline);
        propCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
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
        DashboardLayout.setNodeValue("prop", visionPipeline.getPropLocation().toString());
    }


    @Override
    public void start() {
        visionPipeline.close();
        try {
            propCam.stopStreaming();
        } catch (Exception e) {
            e.printStackTrace();
        }
        propCam.closeCameraDevice();
    }

    @Override
    public void stop() {
        pose = drive.odometry.getPose();
    }

}
