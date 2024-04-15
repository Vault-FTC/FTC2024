package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public abstract class MotionProfile {

    private final ElapsedTime timer;
    final double maxAcceleration;
    final double maxVelocity;

    final double timeInterval;

    ArrayList<Double> setpoints = new ArrayList<>();

    public MotionProfile(double target, double maxAcceleration, double maxVelocity, double timeInterval) {
        timer = new ElapsedTime();
        this.maxAcceleration = maxAcceleration;
        this.maxVelocity = maxVelocity;
        this.timeInterval = timeInterval;
    }

    abstract void generateSetpoints();

}
