package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "red left", group = "red")
public class RedLeft extends AutonLoader {
    @Override
    public void init() {
        loadAuton(AutonType.RED_LEFT);
    }

}
