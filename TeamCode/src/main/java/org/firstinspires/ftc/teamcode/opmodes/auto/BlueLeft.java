package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Left")
public class BlueLeft extends AutonContainer {

    public BlueLeft() {
        super(Alliance.BLUE, blueLeftStartPosition);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = blueLeftCommand;
    }

}
