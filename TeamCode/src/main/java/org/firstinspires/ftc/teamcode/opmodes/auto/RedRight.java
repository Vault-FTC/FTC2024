package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.vision.Pipeline;

@Autonomous(name = "red right", group = "blue")
public class RedRight extends Auton {
    public RedRight() {
        super(Pipeline.Alliance.RED);
    }
}
