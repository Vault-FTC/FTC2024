package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.vision.Pipeline;

@Autonomous(name = "blue right", group = "blue")
public class BlueRight extends Auton {
    public BlueRight() {
        super(Pipeline.Alliance.BLUE);
    }
}
