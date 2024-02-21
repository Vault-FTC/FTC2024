package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private final double kP;
    private final double kI;
    private final double kD;
    private double i;
    private double lastError = 0;
    private double minIntegralErr = 0;
    private double maxIntegralErr = Double.POSITIVE_INFINITY;

    private final ElapsedTime timer;
    private double lastTimestamp = 0;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        timer = new ElapsedTime();
        timer.reset();
    }

    /**
     * Defines the range in which the integral term will accumulate.  If the magnitude of the error is outside of this range, the integral will neither accumulate nor be added to the controller output.
     *
     * @param minErr The minimum magnitude the error needs to be for the integral to accumulate.
     * @param maxErr The maximum magnitude the error needs to be for the integral to accumulate.
     */
    public void setIntegralAccumulationRange(double minErr, double maxErr) {
        minIntegralErr = Math.abs(minErr);
        maxIntegralErr = Math.abs(maxErr);
    }

    private double elapsedTime() {
        double currentTime = timer.milliseconds();
        double elapsedTime = currentTime - lastTimestamp;
        lastTimestamp = currentTime;
        return elapsedTime > 200 ? 50 : elapsedTime;
    }

    public double calculate(double measurement, double setpoint) {
        double error = setpoint - measurement;
        double dt = elapsedTime();
        double p = kP * error;
        double d = -kD * (error - lastError) / dt;
        lastError = error;
        if (Math.abs(error) > Math.abs(minIntegralErr) && Math.abs(error) < Math.abs(maxIntegralErr)) {
            i += kI * error * dt;
            return p + i + d;
        } else {
            return p + d;
        }
    }
}
