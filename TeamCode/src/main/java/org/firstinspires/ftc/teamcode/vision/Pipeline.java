package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.webdashboard.DashboardLayout;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class Pipeline extends OpenCvPipeline {

    private PropLocation propLocation = PropLocation.CENTER;
    private ArrayList<Integer> locationHistory = new ArrayList<>();
    private boolean processing = true;

    @Override
    public Mat processFrame(Mat frame) {

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(frame, frame, 7);
        CLAHE clahe = Imgproc.createCLAHE(5);
        clahe.apply(frame, frame); // Increase contrast
        ArrayList<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(frame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(frame, contours, -1, new Scalar(255, 255, 255), 8);

        Mat circles = new Mat();

        Imgproc.HoughCircles(frame, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 100, 35, 30, 80);

        double xSum = 0;
        double ySum = 0;
        double propX = 0;

        if (!circles.empty()) {
            for (int i = 0; i < circles.cols(); i++) {
                xSum += circles.get(0, i)[0];
                ySum += circles.get(0, i)[1];
            }
            propX = xSum / circles.cols(); // Divide by the number of circles detected
        }

        if (processing) {
            if (circles.empty() || propX > 1240) {
                propLocation = PropLocation.LEFT;
            } else if (propX < 640) {
                propLocation = PropLocation.CENTER;
            } else {
                propLocation = PropLocation.RIGHT;
            }

            locationHistory.add(propLocation.ordinal());
        }

        DashboardLayout.setNodeValue("prop", propX);

        return frame;
    }

    public enum Alliance {
        BLUE,
        RED
    }

    public enum PropLocation {
        LEFT(0),
        CENTER(1),
        RIGHT(2);

        public final Integer location;

        PropLocation(int location) {
            this.location = location;
        }

        @Override
        public String toString() {
            switch (location) {
                case 0:
                    return "left";
                case 1:
                    return "center";
                case 2:
                    return "right";
            }
            return null;
        }
    }

    public PropLocation getPropLocation() {
        return propLocation;
    }

    public PropLocation close() {
        processing = false;
        int sum = 0;
        int i = 0;
        while (i < locationHistory.size() && i < Constants.Vision.lookBehindFrames) {
            sum += locationHistory.get(locationHistory.size() - i - 1);
            i++;
        }
        propLocation = PropLocation.values()[Math.round((float) sum / (float) i)];
        return propLocation;
    }

}
