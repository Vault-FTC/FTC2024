package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.commandsystem.Subsystem;

public class PairedEncoder extends Subsystem implements Encoder {


    DcMotor pairedMotor;
    private int offset = 0;
    private final int polarity;
    private double lastPosition = 0;
    private double lastTimestamp = 0;

    private double velocity = 0;
    ElapsedTime timer = new ElapsedTime();

    public PairedEncoder(DcMotor pairedMotor, boolean reversed) {
        this.pairedMotor = pairedMotor;
        pairedMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pairedMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        polarity = reversed ? -1 : 1;
    }

    public PairedEncoder(DcMotor pairedMotor) {
        this(pairedMotor, false);
    }

    @Override
    public int getPosition() {
        return (pairedMotor.getCurrentPosition() + offset) * polarity;
    }

    @Override
    public double getVelocity() {
        return velocity;
    }

    private double calculateVelocity() {
        double position = getPosition();
        double timestamp = timer.milliseconds();
        double velocity = (position - lastPosition) / (timestamp - lastTimestamp);
        lastPosition = position;
        lastTimestamp = timestamp;
        return velocity;
    }

    @Override
    public void reset() {
        offset = -pairedMotor.getCurrentPosition();
    }

    public void setPosition(int position) {
        offset = -pairedMotor.getCurrentPosition() + position * polarity;
    }

    @Override
    public void periodic() {
        velocity = calculateVelocity();
    }

}
