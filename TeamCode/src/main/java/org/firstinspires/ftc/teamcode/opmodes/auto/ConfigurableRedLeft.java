package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Left")
public class ConfigurableRedLeft extends AutonContainer {

    boolean goToStack;

    public ConfigurableRedLeft() {
        super(Alliance.RED, redLeftStartPosition);
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = redLeftCommand;
    }

}
