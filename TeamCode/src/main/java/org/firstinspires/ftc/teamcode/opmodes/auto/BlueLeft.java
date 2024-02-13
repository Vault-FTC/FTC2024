package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "blue left", group = "blue")
public class BlueLeft extends AutonLoader {
    @Override
    public void init() {
        loadAuton(AutonType.BLUE_LEFT);
    }
}
