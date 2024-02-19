package org.firstinspires.ftc.teamcode.drive;

import java.util.function.Supplier;

public class FutureWaypoint implements WaypointGenerator {

    private final Supplier<Waypoint> waypointSupplier;
    private Waypoint waypoint = null;

    private final boolean canRegenerate;

    public FutureWaypoint(Supplier<Waypoint> waypointSupplier, boolean canRegenerate) {
        this.waypointSupplier = waypointSupplier;
        this.canRegenerate = canRegenerate;
    }

    public FutureWaypoint(Supplier<Waypoint> waypointSupplier) {
        this(waypointSupplier, false);
    }

    @Override
    public Waypoint getWaypoint() {
        if (waypoint == null || canRegenerate) {
            waypoint = waypointSupplier.get();
        }
        return waypoint;
    }

}
