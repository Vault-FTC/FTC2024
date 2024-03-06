package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commandsystem.Command;
import org.firstinspires.ftc.teamcode.drive.Pose2d;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

import java.util.function.Supplier;

public class IntakeDefault extends Command {

    private final Intake intake;
    private final Supplier<Pose2d> poseSupplier;

    public IntakeDefault(Intake intake, Supplier<Pose2d> poseSupplier) {
        this.intake = intake;
        this.poseSupplier = poseSupplier;
        addRequirements(intake);
    }

    @Override
    public void execute() {
        Pose2d botPose = poseSupplier.get();
        if (botPose.x > 95.0 && Math.sin(botPose.rotation.getAngleRadians()) < 0.3) {
            intake.run(Constants.Intake.defaultSpeed);
        } else {
            intake.run(0);
        }
    }
}
