package org.firstinspires.ftc.teamcode.org.rustlib.drive;

import java.util.function.Supplier;

public class FutureInstance<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T path = null;
    private final boolean canRegenerate;

    public FutureInstance(Supplier<T> supplier, boolean canRegenerate) {
        this.supplier = supplier;
        this.canRegenerate = canRegenerate;
    }

    public FutureInstance(Supplier<T> supplier) {
        this(supplier, false);
    }

    @Override
    public T get() {
        if (path == null || canRegenerate) {
            path = supplier.get();
        }
        return path;
    }
}
