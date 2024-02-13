package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private final double kP;
    private final double kI;
    private final double kD;

    private double i;

    private final ElapsedTime timer;
    private double lastTimestamp = 0;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        timer = new ElapsedTime();
        timer.reset();
    }

    private double elapsedTime() {
        double currentTime = timer.milliseconds();
        double elapsedTime = currentTime  - lastTimestamp;
        lastTimestamp = currentTime;
        return elapsedTime > 200 ? 50 : elapsedTime;
    }

    public double calculate(double measurement, double setpoint) {
        double error = setpoint - measurement;
        double dt = elapsedTime();
        double p = kP * error;
        i += kI * error * dt;
        double d = -kD * error / dt;
        return p + i + d;
    }
}
