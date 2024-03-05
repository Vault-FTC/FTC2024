package org.firstinspires.ftc.teamcode.drive;

import org.firstinspires.ftc.teamcode.Constants;

import java.util.ArrayList;

public class Path {
    private final ArrayList<WaypointGenerator> waypoints;

    final double timeout; // In milliseconds

    public Path(double timeout, WaypointGenerator... waypoints) {
        this.waypoints = new ArrayList<>();
        for (WaypointGenerator waypoint : waypoints) {
            this.waypoints.add(waypoint);
        }
        this.timeout = timeout;
    }

    public Path(WaypointGenerator... waypoints) {
        this(Double.POSITIVE_INFINITY, waypoints);
    }

    private Path(Builder builder) {
        waypoints = builder.waypoints;
        timeout = builder.timeout;
    }

    public static class Builder {

        private final ArrayList<WaypointGenerator> waypoints;

        private double defaultRadiusIn = Constants.Drive.defaultFollowRadius;

        private double defaultMaxVelocity = Double.POSITIVE_INFINITY;

        private double timeout = Double.POSITIVE_INFINITY;

        private Builder() {
            waypoints = new ArrayList<>();
        }

        public Builder addWaypoint(WaypointGenerator waypoint) {
            waypoints.add(waypoint);
            return this;
        }

        public Builder setDefaultRadius(double defaultRadiusIn) {
            this.defaultRadiusIn = defaultRadiusIn;
            return this;
        }

        public Builder setDefaultMaxVelocity(double defaultMaxVelocity) {
            this.defaultMaxVelocity = defaultMaxVelocity;
            return this;
        }

        public Builder addWaypoint(double x, double y) {
            waypoints.add(new Waypoint(x, y, defaultRadiusIn, defaultMaxVelocity));
            return this;
        }

        public Builder join(Path path) {
            path.waypoints.forEach((WaypointGenerator waypoint) -> waypoints.add(waypoint));
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

    public Waypoint[] generateWaypoints() {
        ArrayList<Waypoint> generatedWaypoints = new ArrayList<>();
        waypoints.forEach((WaypointGenerator waypoint) -> generatedWaypoints.add(waypoint.getWaypoint()));
        return generatedWaypoints.toArray(new Waypoint[]{});
    }

    public Waypoint[][] getLineSegments() {
        ArrayList<Waypoint[]> segments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {
            segments.add(new Waypoint[]{waypoints.get(i).getWaypoint(), waypoints.get(i + 1).getWaypoint()});
        }
        return segments.toArray(new Waypoint[][]{});
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
