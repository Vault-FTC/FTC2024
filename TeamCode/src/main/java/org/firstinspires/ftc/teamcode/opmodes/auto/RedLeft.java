package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.vision.Pipeline;

@Autonomous(name = "red left", group = "blue")
public class RedLeft extends Auton {
    public RedLeft() {
        super(Pipeline.Alliance.RED);
    }
}
