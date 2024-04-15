package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public class TrapezoidMotionProfile {

    ElapsedTime timer;
    double maxAcceleration;
    double maxVelocity;

    ArrayList<Double> setpoints = new ArrayList<>();

    public TrapezoidMotionProfile(double target, double maxAcceleration, double maxVelocity) {
        timer = new ElapsedTime();
        this.maxAcceleration = maxAcceleration;
        this.maxVelocity = maxVelocity;
    }

    private void generateSetpoints() {

    }


}
