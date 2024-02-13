package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class AutonLoader extends OpMode {

    public enum AutonType {
        BLUE_LEFT,
        BLUE_RIGHT,
        RED_LEFT,
        RED_RIGHT
    }

    OpMode auton;

    public final void loadAuton(AutonType type) {
        auton = new Auton(type, hardwareMap, telemetry, gamepad1, gamepad2);
        auton.init();
    }

    @Override
    public final void init_loop() {
        auton.init_loop();
    }

    @Override
    public final void start() {
        auton.start();
    }

    @Override
    public final void loop() {
        auton.loop();
    }

    @Override
    public final void stop() {
        auton.stop();
    }
}
