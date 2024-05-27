package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Left")
public class RedLeft extends AutonContainer {
    public RedLeft() {
        super(Alliance.RED, redLeftStartPosition);
    }

    @Override
    public void setup() {
        autonomousCommand = redLeftCommand;
    }

}
