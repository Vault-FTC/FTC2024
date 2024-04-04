package org.firstinspires.ftc.teamcode.control;


public class MotionProfileSetpoint {

    double position;
    double velocity;
    double acceleration;

    public MotionProfileSetpoint(double position, double velocity, double acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }
}