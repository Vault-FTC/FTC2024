package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.vision.Pipeline;
import org.firstinspires.ftc.teamcode.vision.Pipeline.Alliance;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
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
        botPose = startPosition;
        fieldCentricOffset = startPosition.rotation;

        propCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "backCam"));
        visionPipeline = new Pipeline();
        propCam.setPipeline(visionPipeline);

        propCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                propCam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                telemetry.addData("Camera opened", "");
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera failed", "");
                telemetry.update();
            }
        });


    }

    @Override
    public void init_loop() {
        telemetry.addData("Wait a few seconds after detection stabilizes to start the program", "");
        telemetry.addData("prop location: ", visionPipeline.getPropLocation().toString());
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
        autonomousCommand.schedule();
    }

}
