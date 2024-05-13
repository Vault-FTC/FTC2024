package org.firstinspires.ftc.teamcode.webdashboard;

import static org.firstinspires.ftc.teamcode.webdashboard.Server.storageDir;

import com.google.gson.JsonParseException;

import org.firstinspires.ftc.teamcode.Constants;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class DashboardLayout {

    public final WebSocket connection;
    private final HashMap<String, Runnable> callbacks = new HashMap<>();
    public ArrayList<DashboardNode> nodes;
    public String id = "";
    public static final String layoutFilePrefix = "dashboard_layout_";

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

    private static JsonObject getSendableNodeData(String id, String value) {
        return Json.createObjectBuilder()
                .add("messageType", "node update")
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
            JsonObject jsonObject = getSendableNodeData(id, value);
            Server.getInstance().sendToConnection(this, jsonObject.toString());
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
            JsonObject jsonObject = getSendableNodeData(id, value);
            Server.getInstance().broadcastJson(jsonObject);
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
        id = object.getString("id");
        for (JsonValue jsonValue : jsonValues) {
            JsonObject node = jsonValue.asJsonObject();
            nodes.add(new DashboardNode(node.getString("id"), getNodeType(node.getString("type")), node.getString("state")));
        }
        this.nodes = nodes;
    }

    public void updateNode(JsonObject object) {
        JsonObject configuration = object.getJsonObject("configuration");
        String nodeID = configuration.getString("id");
        for (DashboardNode node : nodes) {
            if (Objects.equals(node.id, nodeID)) {
                node.state = Objects.requireNonNull(configuration.getString("state"));
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
                    Server.log(e + "\n + Couldn't get double value for id " + id);
                    return defaultValue;
                }
            }
        }
        return defaultValue;
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
        return false;
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
        return "";
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
        return "";
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
            POSITION_GRAPH("position_graph"),
            PATH("path"),
            CAMERA_STREAM("camera steam");

            private final String name;

            Type(String name) {
                this.name = name;
            }
        }
    }

    private JsonArray getLayoutJSON() {
        JsonArrayBuilder draggableDataBuilder = Json.createArrayBuilder();
        for (DashboardNode node : nodes) {
            draggableDataBuilder.add(Json.createObjectBuilder()
                    .add("id", node.id)
                    .add("type", node.type.name)
                    .add("state", node.state)
                    .build());
        }
        return draggableDataBuilder.build();
    }

    public static boolean isDashboardLayoutFile(String fileName) {
        return fileName.contains(layoutFilePrefix);
    }

    public void save() throws IOException {
        JsonObject data = Json.createObjectBuilder()
                .add("messageType", "layout state")
                .add("layout", getLayoutJSON())
                .add("id", id)
                .build();
        String fileName = layoutFilePrefix + id.replace(" ", "_") + ".json";
        File output = new File(storageDir, fileName);
        FileOutputStream fileOut = new FileOutputStream(output.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(fileOut);
        writer.write(data.toString());
        writer.close();
    }

    public static DashboardLayout loadLayout(String fileName) {
        String layoutData = loadString(fileName, ".json");
        DashboardLayout layout = new DashboardLayout(null);
        try {
            layout.update(Json.createReader(new StringReader(layoutData)).readObject());
        } catch (JsonParseException e) {
            Server.log(e.toString());
            e.printStackTrace();
        }
        return layout;
    }

    public static String loadString(String fileName, String fileExtension) {
        fileName = fileName.replace(" ", "_");
        StringBuilder data = new StringBuilder();
        try {
            File filePath = new File(storageDir, fileName + fileExtension);
            FileInputStream input = new FileInputStream(filePath);

            int character;
            while ((character = input.read()) != -1) {
                data.append((char) character);
            }

            return data.toString();
        } catch (IOException e) {
            Server.log(e.toString());
            e.printStackTrace();
        }
        return "";
    }

    public static String loadString(String fileName) {
        return loadString(fileName, ".txt");
    }

    public static double loadDouble(String fileName, double defaultValue) {
        try {
            return Double.parseDouble(loadString(fileName));
        } catch (NumberFormatException e) {
            Server.log(e.toString());
            return defaultValue;
        }
    }

    public static boolean loadBoolean(String fileName) {
        try {
            return Boolean.parseBoolean(loadString(fileName));
        } catch (NumberFormatException e) {
            Server.log(e.toString());
            return false;
        }
    }

}
