package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.subsystems.Lights;

public class FlashLights extends Command {

    private final Lights lights;
    private final int cycleTime;

    public FlashLights(Lights lights, int cycleTime) {
        this.lights = lights;
        this.cycleTime = cycleTime;
    }

    @Override
    public void execute() {
        if (Math.sin(timeSinceInitialized() * 2 * Math.PI / cycleTime) > 0) {
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
        } else {
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GOLD);
        }
    }
}
