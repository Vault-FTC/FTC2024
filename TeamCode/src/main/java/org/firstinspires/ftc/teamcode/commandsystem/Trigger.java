package org.firstinspires.ftc.teamcode.commandsystem;

import java.util.function.BooleanSupplier;

public class Trigger implements BooleanSupplier {
    private final BooleanSupplier condition;
    private boolean lastState = false;

    public Trigger(BooleanSupplier condition) {
        this.condition = condition;
    }

    public Trigger() {
        this(() -> false);
    }

    public Trigger whileTrue(Command command) {
        command.triggers.add(this);
        return this;
    }

    public Trigger onTrue(Command command) {
        command.triggers.add(new Trigger(() -> !lastState && getAsBoolean()));
        return this;
    }

    public Trigger onFalse(Command command) {
        command.triggers.add(new Trigger(() -> lastState && !getAsBoolean()));
        return this;
    }

    public Trigger and(Trigger trigger) {
        return new Trigger(() -> getAsBoolean() && trigger.getAsBoolean());
    }

    public Trigger or(Trigger trigger) {
        return new Trigger(() -> getAsBoolean() || trigger.getAsBoolean());
    }

    public Trigger andNot(Trigger trigger) {
        return new Trigger(() -> getAsBoolean() && !trigger.getAsBoolean());
    }

    @Override
    public boolean getAsBoolean() {
        lastState = condition.getAsBoolean();
        return lastState;
    }
}
