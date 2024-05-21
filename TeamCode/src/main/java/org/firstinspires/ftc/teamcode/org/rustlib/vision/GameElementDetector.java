package org.firstinspires.ftc.teamcode.org.rustlib.vision;

import org.firstinspires.ftc.teamcode.opmodes.Robot.GameElementLocation;
import org.openftc.easyopencv.OpenCvPipeline;

public abstract class GameElementDetector extends OpenCvPipeline {
    public abstract void close();

    public abstract GameElementLocation getElementLocation();

    public static class StreamDimension {
        public final int width;
        public final int height;

        public StreamDimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
