package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

public class Lights {
    private final RevBlinkinLedDriver ledDriver;

    public Lights(RevBlinkinLedDriver ledDriver) {
        this.ledDriver = ledDriver;
    }

    public void drivingToBackdropPattern() {
        ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BEATS_PER_MINUTE_RAINBOW_PALETTE);
    }

    public void setPattern(RevBlinkinLedDriver.BlinkinPattern pattern) {
        ledDriver.setPattern(pattern);
    }
}
