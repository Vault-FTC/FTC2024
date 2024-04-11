package org.firstinspires.ftc.teamcode.webdashboard;

import static org.firstinspires.ftc.teamcode.Constants.storageDir;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.ThreadPool;

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
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class Server extends WebSocketServer {

    private static Server instance = null;

    public static final int port = 5837;

    ElapsedTime timer;

    ArrayList<DashboardLayout> layouts = new ArrayList<>();

    private static final DashboardLayout emptyLayout = new EmptyLayout();

    public DashboardLayout firstConnectedLayout() {
        if (layouts.size() > 0) return layouts.get(0);
        else return emptyLayout;
    }

    public DashboardLayout getLayout(String id) {
        for (DashboardLayout layout : layouts) {
            log(layout.id + " " + id);
            if (Objects.equals(layout.id, id)) {
                return layout;
            }
        }
        return emptyLayout;
    }

    private Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        timer = new ElapsedTime();
        start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        layouts.add(new DashboardLayout(conn));
        newLog();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        layouts.removeIf(layout -> conn == layout.connection);
    }

    private DashboardLayout getLayout(WebSocket conn) {
        for (DashboardLayout layout : layouts) {
            if (layout.connection == conn) { // In this case the Objects.equals() method is not ideal.  What's important is that the references are the same, not the values of the variables
                return layout;
            }
        }
        return null;
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
            String messageType = object.getString("messageType");

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
                        savePath(message, FileType.PATH);
                        layout.createNotice("Saved path to robot", DashboardLayout.NoticeType.POSITIVE, 8000);
                    } catch (IOException e) {
                        log(e.toString());
                    }
                    break;
                case "value update":
                    try {
                        savePath(message, FileType.VALUE);
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

    public void newLog() {
        JsonObject message = Json.createObjectBuilder()
                .add("messageType", "reset log")
                .build();
        ThreadPool.getDefaultScheduler().submit(() -> broadcast(message.toString()));
    }

    public void log(String value) {
        JsonObject message = Json.createObjectBuilder()
                .add("messageType", "log")
                .add("value", timer.milliseconds() + ": " + value)
                .build();
        ThreadPool.getDefaultScheduler().submit(() -> broadcast(message.toString()));
    }

    private static void savePath(JsonObject object, FileType fileType) throws IOException {
        JsonObject configuration = object.getJsonObject("configuration");
        JsonObject path = configuration.getJsonObject(fileType.name);
        String fileName = configuration.getString("id").replace(" ", "_") + ".json";
        File output = new File(storageDir, fileName);
        FileOutputStream fileOut = new FileOutputStream(output.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(fileOut);
        writer.write(path.toString());
        writer.close();
    }

    enum FileType {
        PATH("path"),
        VALUE("input");

        final String name;

        FileType(String name) {
            this.name = name;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void start() {
        if (Constants.debugMode) {
            super.start();
        }
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(3);
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