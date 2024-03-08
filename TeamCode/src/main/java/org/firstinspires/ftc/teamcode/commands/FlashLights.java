package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Lights;

public class FlashLights extends Command {

    private final Lights lights;
    private final int cycleTime;

    private final RevBlinkinLedDriver.BlinkinPattern pattern;

    public FlashLights(Lights lights, int cycleTime, RevBlinkinLedDriver.BlinkinPattern pattern) {
        this.lights = lights;
        this.cycleTime = cycleTime;
        this.pattern = pattern;
    }

    public FlashLights(Lights lights, int cycleTime) {
        this(lights, cycleTime, RevBlinkinLedDriver.BlinkinPattern.GREEN);
    }

    @Override
    public void execute() {
        if (Math.sin(timeSinceInitialized() * 2 * Math.PI / cycleTime) > 0) {
            lights.setPattern(pattern);
        } else {
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
