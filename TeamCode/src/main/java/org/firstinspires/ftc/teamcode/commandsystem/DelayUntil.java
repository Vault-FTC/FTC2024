package org.firstinspires.ftc.teamcode.commandsystem;

import java.util.function.BooleanSupplier;

public class DelayUntil extends Command {
    private final BooleanSupplier condition;

    private final double timeout;
    public DelayUntil(BooleanSupplier condition, double timeout) {
        this.condition = condition;
        this.timeout = timeout;
    }

    public DelayUntil(BooleanSupplier condition) {
        this(condition, Double.POSITIVE_INFINITY);
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean isFinished() {
        return condition.getAsBoolean() || timeSinceInitialized() > timeout;
    }
}
