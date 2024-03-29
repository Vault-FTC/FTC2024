package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;

public class PairedEncoder implements Encoder {

    DcMotor pairedMotor;
    private int offset = 0;

    private final int polarity;

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
    public void reset() {
        offset = -pairedMotor.getCurrentPosition();
    }

    public void setPosition(int position) {
        offset = -pairedMotor.getCurrentPosition() + position * polarity;
    }


}
