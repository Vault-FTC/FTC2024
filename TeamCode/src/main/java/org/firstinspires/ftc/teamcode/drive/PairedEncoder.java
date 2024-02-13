package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;

import java.text.Format;

public class PairedEncoder implements Encoder {

    DcMotor pairedMotor;
    private int offset = 0;

    private final int polarity;

    public PairedEncoder(DcMotor pairedMotor, boolean reversed) {
        this.pairedMotor = pairedMotor;
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
    public void reset() {
        offset = -pairedMotor.getCurrentPosition();
    }


}
