package org.firstinspires.ftc.teamcode.drive;

import static org.firstinspires.ftc.teamcode.Constants.storageDir;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.webdashboard.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Path {
    public final ArrayList<WaypointGenerator> waypoints;

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

    public Waypoint[][] generateLineSegments() {
        ArrayList<Waypoint[]> segments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {
            segments.add(new Waypoint[]{waypoints.get(i).getWaypoint(), waypoints.get(i + 1).getWaypoint()});
        }
        return segments.toArray(new Waypoint[][]{});
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    private static Rotation2d getRotation(String data) {
        double angle;
        try {
            angle = Double.parseDouble(data);
            return new Rotation2d(angle);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public double getTimeout() {
        return timeout;
    }

    private static double parseTimeout(String data) {
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            return Double.POSITIVE_INFINITY;
        }
    }

    public Path appendWaypoint(WaypointGenerator waypoint, double timeout) {
        return join(new Path(timeout, waypoint));
    }

    public Path join(Path path) {
        return join(path, path.timeout + timeout);
    }

    /**
     * Creates a new path by joining the specified path to the end of this path instance.
     *
     * @param path
     * @param timeout
     * @return
     */
    public Path join(Path path, double timeout) {
        Builder builder = getBuilder().setTimeout(timeout);
        for (WaypointGenerator waypoint : waypoints) {
            builder.addWaypoint(waypoint);
        }
        for (WaypointGenerator waypoint : path.waypoints) {
            builder.addWaypoint(waypoint);
        }
        return builder.build();
    }

    public static Path loadPath(String fileName) {
        StringBuilder data = new StringBuilder();
        Builder pathBuilder = Path.getBuilder();
        try {
            File filePath = new File(storageDir, fileName + ".json");
            FileInputStream input = new FileInputStream(filePath);

            int character;
            while ((character = input.read()) != -1) {
                data.append((char) character);
            }

            JsonReader reader = Json.createReader(new StringReader(data.toString()));
            JsonObject path = reader.readObject();
            Server.getInstance().log(path.toString());
            double timeout = parseTimeout(path.getString("timeout"));
            JsonArray array = path.getJsonArray("points");
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.getJsonObject(i);
                Server.getInstance().log(object.toString());

                JsonObject fieldVector = object.getJsonObject("fieldVector");
                double x = fieldVector.getJsonNumber("x").doubleValue();
                double y = fieldVector.getJsonNumber("y").doubleValue();
                double followRadius = object.getJsonNumber("followRadius").doubleValue();
                Rotation2d targetFollowRotation = getRotation(object.get("targetFollowRotation").toString());
                Rotation2d targetEndRotation = getRotation(object.get("targetEndRotation").toString());
                double maxVelocity;
                try {
                    maxVelocity = object.getJsonNumber("maxVelocity").doubleValue();
                } catch (NullPointerException e) {
                    maxVelocity = Double.POSITIVE_INFINITY;
                }

                pathBuilder.addWaypoint(new Waypoint(x, y, followRadius, targetFollowRotation, targetEndRotation, maxVelocity));
            }
            Path loaded = pathBuilder.setTimeout(timeout).build();
            Server.getInstance().log("fileName: ");
            for (WaypointGenerator waypointGenerator : loaded.waypoints) {
                Server.getInstance().log(waypointGenerator.toString());
            }
            return loaded;
        } catch (IOException e) {
            Server.getInstance().log(e.toString());
            e.printStackTrace();
        }
        return new Path();
    }
}
