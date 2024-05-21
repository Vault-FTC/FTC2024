package org.firstinspires.ftc.teamcode.opmodes.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.opmodes.Robot;
import org.firstinspires.ftc.teamcode.org.rustlib.commandsystem.AutonomousCommand;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.org.rustlib.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Server;
import org.firstinspires.ftc.teamcode.org.rustlib.vision.DetectorPipeline;
import org.firstinspires.ftc.teamcode.org.rustlib.vision.GameElementDetector;
import org.firstinspires.ftc.teamcode.org.rustlib.vision.GameElementDetector.StreamDimension;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Auton extends Robot {
    OpenCvWebcam detectorCam;
    GameElementDetector detectorPipeline;
    AutonomousCommand autonomousCommand;

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
        if (alliance == Alliance.BLUE) {
            fieldCentricOffset = new Rotation2d();
        } else {
            fieldCentricOffset = new Rotation2d(Math.PI);
        }

        initializeDetectorCam("propCam", new StreamDimension(1280, 720));
    }

    @Override
    public void init_loop() {
        telemetry.addData("Wait a few seconds after detection stabilizes to start the program.", "");
        telemetry.addData("prop location: ", detectorPipeline.getElementLocation().toString());
    }


    @Override
    public void start() {
        detectorPipeline.close();
        try {
            detectorCam.stopStreaming();
            detectorCam.closeCameraDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        autonomousCommand.schedule();
    }

    private void initializeDetectorCam(String cameraName, StreamDimension size) {
        detectorCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, cameraName));
        detectorPipeline = new DetectorPipeline();
        detectorCam.setPipeline(detectorPipeline);
        detectorCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                detectorCam.startStreaming(size.width, size.height);
                String message = "Camera opened";
                telemetry.addData(message, "");
                telemetry.update();
                Server.log(message);
            }

            @Override
            public void onError(int errorCode) {
                String message = "Camera initialization failed";
                telemetry.addData(message, "");
                telemetry.update();
                Server.log(message);
            }
        });
    }
}
