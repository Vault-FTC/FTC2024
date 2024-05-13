package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Left")
public class ConfigurableBlueRight extends AutonContainer {

    public ConfigurableBlueRight() {
        super(Alliance.BLUE, blueRightStartPosition);
    }

    @Override
    public void init() {
        super.init();
        autonomousCommand = blueRightCommand;
    }

}
