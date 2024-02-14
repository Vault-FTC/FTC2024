package org.firstinspires.ftc.teamcode.drive;

import java.util.ArrayList;

public class Path {
    private final ArrayList<Waypoint> waypoints;

    public final double timeout; // In milliseconds

    private Path(Builder builder) {
        waypoints = builder.waypoints;
        timeout = builder.timeout;
    }

    public static class Builder {

        private final ArrayList<Waypoint> waypoints;

        private double defaultRadiusIn = 12;

        private double timeout = Double.POSITIVE_INFINITY;

        private Builder() {
            waypoints = new ArrayList<>();
        }

        public Builder addWaypoint(Waypoint waypoint) {
            waypoints.add(waypoint);
            return this;
        }

        public Builder setDefaultRadius(double defaultRadiusIn) {
            this.defaultRadiusIn = defaultRadiusIn;
            return this;
        }

        public Builder addWaypoint(double x, double y) {
            waypoints.add(new Waypoint(x, y, defaultRadiusIn));
            return this;
        }

        public Builder setTimeout(double timeout) {
            this.timeout = timeout;
            return this;
        }

        public Path build() {
            return new Path(this);
        }
    }

    public Waypoint[][] getLineSegments() {
        ArrayList<Waypoint[]> segments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {
            segments.add(new Waypoint[]{waypoints.get(i), waypoints.get(i + 1)});
        }
        return segments.toArray(new Waypoint[][]{});
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
