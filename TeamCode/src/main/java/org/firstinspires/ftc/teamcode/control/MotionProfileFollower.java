package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MotionProfileFollower {

    ElapsedTime timer;
    ArrayList<Double> setpoints;
    PIDController pidController;
    Consumer<Double> driveSystem;

    public MotionProfileFollower(ArrayList<Double> setpoints, PIDController pidController, Consumer<Double> driveSystem) {
        this.setpoints = setpoints;
        this.pidController = pidController;
        this.driveSystem = driveSystem;
    }

    public void follow() {

    }
}
