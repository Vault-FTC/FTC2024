package org.firstinspires.ftc.teamcode.webdashboard;

import android.os.Environment;
import android.util.Pair;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.teamcode.Constants;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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


public class Server extends WebSocketServer {
    private static Server instance = null;
    public static final int port = 5837;
    public static final File storageDir = new File(Environment.getExternalStorageDirectory() + "/Download");
    private ArrayList<DashboardLayout> layouts = new ArrayList<>();
    private static final DashboardLayout emptyLayout = new EmptyLayout();
    private ArrayList<Pair<String, String>> messageQueue = new ArrayList<>();
    ElapsedTime timer;

    private Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        timer = new ElapsedTime();
    }

    @Override
    public void start() {
        messageQueue.clear();
        layouts.addAll(loadLayouts());
        if (Constants.debugMode) {
            super.start();
        }
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        for (DashboardLayout layout : layouts) {
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
        layouts.add(new DashboardLayout(conn));
        HashMap<String, DashboardLayout> duplicates = new HashMap<>();
        ArrayList<DashboardLayout> toRemove = new ArrayList<>();
        for (DashboardLayout layout : layouts) { // Check for layouts with the same id and remove a duplicate if its connection is not open.  If a layout has a closed connection but no duplicate, it will be kept.
            DashboardLayout first = duplicates.get(layout.id);
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
            DashboardLayout layout = getLayout(conn);
            assert layout != null;

            JsonReader reader = Json.createReader(new StringReader(data));
            JsonObject object = reader.readObject();
            JsonObject message = object.getJsonObject("message");
            String messageType = message.getString("messageType");

            switch (messageType) {
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
                        layout.createNotice("Saved path to robot", DashboardLayout.NoticeType.POSITIVE, 8000);
                    } catch (IOException e) {
                        log(e.toString());
                    }
                    break;
                case "value update":
                    try {
                        saveValue(message);
                        layout.createNotice("Saved value to robot", DashboardLayout.NoticeType.POSITIVE, 8000);
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

    public static DashboardLayout getLayout(String id) {
        for (DashboardLayout layout : getInstance().layouts) {
            if (Objects.equals(layout.id, id)) {
                return layout;
            }
        }
        return emptyLayout;
    }

    private DashboardLayout getLayout(WebSocket conn) {
        for (DashboardLayout layout : layouts) {
            if (layout.connection == conn) { // In this case the Objects.equals() method is not ideal.  What's important is that the references are the same, not the values of the variables
                return layout;
            }
        }
        return emptyLayout;
    }

    private ArrayList<DashboardLayout> loadLayouts() {
        ArrayList<DashboardLayout> layouts = new ArrayList<>();
        for (File file : storageDir.listFiles()) {
            if (DashboardLayout.isDashboardLayoutFile(file.getName())) {
                layouts.add(DashboardLayout.loadLayout(file.getName()));
            }
        }
        return layouts;
    }

    public boolean connected() {
        boolean connected = false;
        for (DashboardLayout layout : layouts) {
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

    public void sendToConnection(DashboardLayout layout, String message) {
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

    public void log(Object value) {
        log(value.toString());
    }

    public void log(String value) {
        JsonObject message = Json.createObjectBuilder()
                .add("messageType", "log")
                .add("value", timer.milliseconds() + ": " + value)
                .build();
        broadcastJson(message);
    }

    private static void savePath(JsonObject object) throws IOException {
        JsonObject configuration = object.getJsonObject("configuration");
        JsonObject path = configuration.getJsonObject("path");
        String fileName = configuration.getString("id").replace(" ", "_") + ".json";
        File output = new File(storageDir, fileName);
        FileOutputStream fileOut = new FileOutputStream(output.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(fileOut);
        writer.write(path.toString());
        writer.close();
    }

    private static void saveValue(JsonObject object) throws IOException {
        JsonObject configuration = object.getJsonObject("configuration");
        String value = configuration.getString("input");
        String fileName = configuration.getString("id").replace(" ", "_") + ".txt";
        File output = new File(storageDir, fileName);
        FileOutputStream fileOut = new FileOutputStream(output.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(fileOut);
        writer.write(value);
        writer.close();
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

    public static Server getInstance() {
        if (instance == null) {
            try {
                instance = new Server(port);
                RobotLog.v("dashboard server started");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}