package org.firstinspires.ftc.teamcode.org.rustlib.rustboard;

import android.os.Environment;
import android.util.Pair;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.teamcode.constants.SubsystemConstants;
import org.firstinspires.ftc.teamcode.org.rustlib.config.Loader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Rustboard extends WebSocketServer {
    private static Rustboard instance = null;
    public static final int port = 21865;
    private static final File storageDir = new File(Environment.getExternalStorageDirectory() + "\\Download");
    public static final File timeCalibration = new File(Loader.defaultStorageDirectory + "\\.Time\\.time_offset.txt");
    protected ArrayList<RustboardLayout> layouts = new ArrayList<>();
    private static final RustboardLayout emptyLayout = new EmptyLayout();
    private ArrayList<Pair<String, String>> messageQueue = new ArrayList<>();
    private long timeOffset = 0;
    ElapsedTime timer;

    private Rustboard(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        timer = new ElapsedTime();
    }

    @Override
    public void start() {
        messageQueue.clear();
        layouts.addAll(loadLayouts());
        if (SubsystemConstants.debugMode) {
            super.start();
        }
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        for (RustboardLayout layout : layouts) {
            layout.save();
        }
        super.stop();
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(3);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log("client " + conn.getRemoteSocketAddress().toString() + " connected to the robot.");
        layouts.add(new RustboardLayout(conn));
        HashMap<String, RustboardLayout> duplicates = new HashMap<>();
        ArrayList<RustboardLayout> toRemove = new ArrayList<>();
        for (RustboardLayout layout : layouts) { // Check for layouts with the same id and remove a duplicate if its connection is not open.  If a layout has a closed connection but no duplicate, it will be kept.
            RustboardLayout first = duplicates.get(layout.id);
            if (first == null) {
                duplicates.put(layout.id, layout);
            } else { // If this block is reached, then there are two layouts with the same id
                if (layout.connection == null || layout.connection.isClosed()) {
                    toRemove.add(layout);
                } else {
                    toRemove.add(first);
                }
            }
        }
        toRemove.forEach((layout) -> layouts.remove(layout));
        sendQueuedMessages();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log("client " + conn.getRemoteSocketAddress().toString() + " disconnected from the robot.");
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        if (Objects.equals(data, "ping")) {
            conn.send("pong");
        } else {
            RustboardLayout layout = getLayout(conn);
            JsonReader reader = Json.createReader(new StringReader(data));
            JsonObject object = reader.readObject();
            JsonObject message = object.getJsonObject("message");
            String messageType = message.getString("messageType");
            switch (messageType) {
                case "calibrate time":
                    setTimeOffset(message);
                    break;
                case "layout state":
                    layout.update(message);
                    layout.id = message.getString("id");
                    break;
                case "node update":
                    layout.updateNode(message);
                    break;
                case "path update":
                    try {
                        savePath(message);
                        layout.createNotice("Saved path to robot", RustboardLayout.NoticeType.POSITIVE, 8000);
                    } catch (IOException e) {
                        log(e.toString());
                    }
                    break;
                case "value update":
                    try {
                        saveValue(message);
                        layout.createNotice("Saved value to robot", RustboardLayout.NoticeType.POSITIVE, 8000);
                    } catch (IOException e) {
                        log(e.toString());
                    }
                    break;
                case "click":
                    layout.buttonClicked(message.getString("nodeID"));
                    break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        log(e);
    }

    private void calibrateTime() {
        if (timeCalibration.exists()) {
            timeOffset = Long.parseLong(Loader.loadString(timeCalibration));
        } else {
            try {
                Loader.writeString(timeCalibration, "0");
            } catch (IOException e) {
                log(e);
            }
        }
    }

    private void setTimeOffset(JsonObject object) {
        String offset = object.getString("offset");
        timeOffset = Long.parseLong(offset);
        try {
            Loader.writeString(timeCalibration, offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected long getUTCTime() {
        return System.currentTimeMillis() + timeOffset;
    }

    public static RustboardLayout getLayout(String id) {
        for (RustboardLayout layout : getInstance().layouts) {
            if (Objects.equals(layout.id, id)) {
                return layout;
            }
        }
        return emptyLayout;
    }

    private RustboardLayout getLayout(WebSocket conn) {
        for (RustboardLayout layout : layouts) {
            if (layout.connection == conn) {
                return layout;
            }
        }
        return emptyLayout;
    }

    private ArrayList<RustboardLayout> loadLayouts() {
        ArrayList<RustboardLayout> layouts = new ArrayList<>();
        for (File file : storageDir.listFiles()) {
            if (RustboardLayout.isDashboardLayoutFile(file.getName())) {
                layouts.add(RustboardLayout.loadLayout(file.getName()));
            }
        }
        return layouts;
    }

    public boolean connected() {
        boolean connected = false;
        for (RustboardLayout layout : layouts) {
            if (layout.connection != null) {
                connected = true;
            }
        }
        return connected;
    }

    private void sendQueuedMessages() {
        ArrayList<Pair<String, String>> toRemove = new ArrayList<>();
        for (Pair<String, String> message : messageQueue) {
            if (message.first == null) { // If no layout id is given, broadcast to all layouts
                ThreadPool.getDefaultScheduler().submit(() -> broadcast(message.second));
            } else {
                WebSocket connection = getLayout(message.first).connection;
                if (connection != null) {
                    ThreadPool.getDefaultScheduler().submit(() -> connection.send(message.second));
                    toRemove.add(message); // So the collection isn't being modified in the for loop
                }
            }
        }
        toRemove.forEach((message) -> messageQueue.remove(message));
    }

    public void sendToConnection(RustboardLayout layout, String message) {
        if (connected()) {
            ThreadPool.getDefaultScheduler().submit(() -> layout.connection.send(message));
        } else {
            messageQueue.add(new Pair<>(layout.id, message));
        }
    }

    public void broadcastJson(JsonObject json) {
        if (connected()) {
            ThreadPool.getDefaultScheduler().submit(() -> broadcast(json.toString()));
        } else { // The server sends 3 types of messages to clients: node update, notify, and log
            String messageType = json.getString("messageType");
            if (Objects.equals(messageType, "node update") || messageType.equals("log")) {
                messageQueue.add(new Pair<>(null, json.toString()));
            }
        }
    }

    public void newLog() {
        JsonObject message = Json.createObjectBuilder()
                .add("messageType", "reset log")
                .build();
        broadcastJson(message);
    }

    public static void log(Object value) {
        getInstance().log(value.toString());
    }

    public static void log(String value) {
        JsonObject message = Json.createObjectBuilder()
                .add("messageType", "log")
                .add("value", getInstance().timer.milliseconds() + ": " + value)
                .build();
        getInstance().broadcastJson(message);
    }

    private static void saveValue(JsonObject object, String fileExtension) throws IOException {
        JsonObject configuration = object.getJsonObject("configuration");
        String value = configuration.getString("input");
        String fileName = configuration.getString("id").replace(" ", "_") + "." + fileExtension;
        File output = new File(storageDir, fileName);
        Loader.writeString(output, value);
    }

    private static void saveValue(JsonObject object) throws IOException {
        saveValue(object, "txt");
    }

    private static void savePath(JsonObject object) throws IOException {
        saveValue(object, "json");
    }

    private static void clearStorage() {
        File[] files = storageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public void startOpMode(String opModeName) {
        RegisteredOpModes.getInstance().getOpMode(opModeName).init();
    }

    public List<OpModeMeta> getRegisteredOpModes() {
        return RegisteredOpModes.getInstance().getOpModes();
    }

    public static Rustboard getInstance() {
        if (instance == null) {
            try {
                instance = new Rustboard(port);
                RobotLog.v("dashboard server started");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}