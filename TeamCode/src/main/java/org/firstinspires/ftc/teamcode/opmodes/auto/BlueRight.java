package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "blue right", group = "blue")
public class BlueRight extends AutonLoader {
    @Override
    public void init() {
        loadAuton(AutonType.BLUE_RIGHT);
    }
}
