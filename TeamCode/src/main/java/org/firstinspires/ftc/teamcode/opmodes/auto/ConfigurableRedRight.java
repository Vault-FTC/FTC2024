package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Right")
public class ConfigurableRedRight extends AutonContainer {

    public ConfigurableRedRight() {
        super(Alliance.RED, redRightStartPosition);
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = redRightCommand;
    }

}
