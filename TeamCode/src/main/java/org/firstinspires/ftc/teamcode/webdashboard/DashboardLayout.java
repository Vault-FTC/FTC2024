package org.firstinspires.ftc.teamcode.webdashboard;

import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.teamcode.Constants;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class DashboardLayout {

    public final WebSocket connection;
    private final HashMap<String, Runnable> callbacks = new HashMap<>();
    public ArrayList<DashboardNode> nodes;
    public String id = "";

    public DashboardLayout(WebSocket connection) {
        this.connection = connection;
    }

    private static DashboardNode.Type getNodeType(String inputType) {
        DashboardNode.Type type = null;
        DashboardNode.Type[] types = DashboardNode.Type.values();
        for (DashboardNode.Type value : types) {
            if (Objects.equals(value.name, inputType)) {
                type = value;
            }
        }
        return type;
    }

    private static JsonObject getNodeData(String id, String value) {
        return Json.createObjectBuilder()
                .add("messageType", "update")
                .add("nodeID", id)
                .add("state", value)
                .build();
    }

    /**
     * Sets the value of a node linked to this dashboard.
     *
     * @param id    The id of the target dashboard node.
     * @param value The value to send to the target dashboard node.
     */
    public void setMyNodeValue(String id, String value) {
        if (Constants.debugMode) {
            JsonObject jsonObject = getNodeData(id, value);
            ThreadPool.getDefaultScheduler().submit(() -> connection.send(jsonObject.toString()));
        }
    }

    /**
     * Sets the value of a node linked to this dashboard.
     *
     * @param id    The id of the target dashboard node.
     * @param value The value to send to the target dashboard node.
     */
    public void setMyNodeValue(String id, Object value) {
        setNodeValue(id, String.valueOf(value));
    }

    /**
     * Sets the value of every connected dashboard node that has the corresponding id.  Be careful!  Multiple dashboards may have nodes of different types and the same id.
     *
     * @param id    The id of the target dashboard nodes.
     * @param value The value to send to the target dashboard nodes.
     */
    public static void setNodeValue(String id, String value) {
        if (Constants.debugMode) {
            JsonObject jsonObject = getNodeData(id, value);
            ThreadPool.getDefaultScheduler().submit(() -> Server.getInstance().broadcast(jsonObject.toString()));
        }
    }

    /**
     * Sets the value of every connected dashboard node that has the corresponding id.  Be careful!  Multiple dashboards may have nodes of different types and the same id.
     *
     * @param id    The id of the target dashboard nodes.
     * @param value The value to send to the target dashboard nodes.
     */
    public static void setNodeValue(String id, Object value) {
        setNodeValue(id, String.valueOf(value));
    }

    public void update(JsonObject object) {
        ArrayList<DashboardNode> nodes = new ArrayList<>();
        JsonArray jsonValues = object.getJsonArray("layout");
        id = object.getJsonString("id").toString();
        for (JsonValue jsonValue : jsonValues) {
            JsonObject node = jsonValue.asJsonObject();
            nodes.add(new DashboardNode(node.getString("id"), getNodeType(node.getString("type")), String.valueOf(node.get("state"))));
        }
        this.nodes = nodes;
    }

    public void updateNode(JsonObject object) {
        JsonObject configuration = object.getJsonObject("configuration");
        String nodeID = configuration.getString("id");
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, nodeID)) {
                node.state = ((JsonString) Objects.requireNonNull(configuration.get("state"))).getString();
                return;
            }
        }
    }

    public double getDoubleValue(String id, double defaultValue) {
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, id)) {
                if (node.type != DashboardNode.Type.TEXT_INPUT) {
                    throw new IllegalArgumentException("Requested node is not an input");
                }
                try {
                    return Double.parseDouble(node.state);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        throw new IllegalArgumentException("Requested node does not exist");
    }

    public boolean getBooleanValue(String id) {
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, id)) {
                if (!(node.type == DashboardNode.Type.BOOLEAN_TELEMETRY || node.type == DashboardNode.Type.TOGGLE)) {
                    throw new IllegalArgumentException("Requested node does not use boolean states");
                }
                return Boolean.parseBoolean(node.state);
            }
        }
        throw new IllegalArgumentException("Requested node does not exist");
    }

    public String getInputValue(String id) {
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, id)) {
                if (node.type != DashboardNode.Type.TEXT_INPUT) {
                    throw new IllegalArgumentException("Requested node is not an input");
                }
                return node.state;
            }
        }
        throw new IllegalArgumentException("Requested node does not exist");
    }

    public String getSelectedValue(String id) {
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, id)) {
                if (node.type != DashboardNode.Type.SELECTOR) {
                    throw new IllegalArgumentException("Requested node is not a selector");
                }
                return node.state;
            }
        }
        throw new IllegalArgumentException("Requested node does not exist");
    }

    public void buttonClicked(String id) {
        try {
            Objects.requireNonNull(callbacks.get(id)).run();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void addCallback(String buttonName, Runnable callback) {
        callbacks.put(buttonName, callback);
    }

    public void createNotice(String notice, NoticeType type, int durationMilliseconds) {
        JsonObject data = Json.createObjectBuilder()
                .add("messageType", "notify")
                .add("message", notice)
                .add("type", type.value)
                .add("duration", durationMilliseconds)
                .build();
        connection.send(data.toString());
    }

    public enum NoticeType {
        POSITIVE("positive"),
        NEGATIVE("negative"),
        NEUTRAL("neutral");

        String value;

        NoticeType(String value) {
            this.value = value;
        }
    }

    public static class DashboardNode {

        private final String id;
        private final Type type;
        private String state;

        public DashboardNode(String id, Type type, String state) {
            this.id = id;
            this.type = type;
            this.state = state;
        }

        public enum Type {
            BUTTON("button"),
            TOGGLE("toggle"),
            SELECTOR("selector"),
            BOOLEAN_TELEMETRY("boolean telemetry"),
            TEXT_TELEMETRY("text telemetry"),
            TEXT_INPUT("text input"),
            CAMERA_STREAM("camera steam");

            private final String name;

            Type(String name) {
                this.name = name;
            }
        }
    }

}
