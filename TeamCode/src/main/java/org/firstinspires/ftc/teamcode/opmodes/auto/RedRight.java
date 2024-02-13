package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "red right", group = "red")
public class RedRight extends AutonLoader {
    @Override
    public void init() {
        loadAuton(AutonType.RED_RIGHT);
    }
}
