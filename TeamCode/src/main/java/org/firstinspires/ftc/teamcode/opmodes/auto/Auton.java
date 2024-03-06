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
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Auton extends Robot {

    OpenCvWebcam propCam;
    Pipeline visionPipeline;
    Command autonomousCommand;

    private final Rotation2d rotationOffset;

    public Auton(Alliance alliance, Rotation2d rotationOffset) {
        Robot.alliance = alliance;
        this.rotationOffset = rotationOffset;
    }

    @Override
    public void init() {
        super.init();
        drive.setFieldCentricOffset(rotationOffset);

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
