package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Left")
public class ConfigurableBlueLeft extends AutonContainer {

    public ConfigurableBlueLeft() {
        super(Alliance.BLUE, blueLeftStartPosition);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = blueLeftCommand;
    }

}
