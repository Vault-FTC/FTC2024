package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Right")
public class RedRight extends AutonContainer {

    public RedRight() {
        super(Alliance.RED, redRightStartPosition);
    }


    @Override
    public void init() {
        super.init();
        autonomousCommand = redRightCommand;
    }

}
