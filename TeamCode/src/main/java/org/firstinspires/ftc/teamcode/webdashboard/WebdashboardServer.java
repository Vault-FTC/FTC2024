package org.firstinspires.ftc.teamcode.webdashboard;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.Constants;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class WebdashboardServer extends WebSocketServer {

    ArrayList<DashboardLayout> layouts = new ArrayList<>();

    public DashboardLayout getFirstConnectedLayout() {
        if (layouts.size() > 0) return layouts.get(0);
        else return null;
    }

    private static WebdashboardServer instance = null;

    public static final int port = 5837;

    private WebdashboardServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        layouts.add(new DashboardLayout(conn));

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
    public void onMessage(WebSocket conn, String message) {
        if (Objects.equals(message, "ping")) {
            conn.send("pong");
        } else {
            DashboardLayout layout = getLayout(conn);
            assert layout != null;

            JsonReader reader = Json.createReader(new StringReader(message));
            JsonObject object = reader.readObject().getJsonObject("message");

            if (Objects.equals(object.getString("messageType"), "layout state")) {
                if (!layouts.isEmpty()) {
                    layout.update(object);
                }
            } else if (Objects.equals(object.getString("messageType"), "node update")) {
                layout.updateNode(object);
            } else if (Objects.equals(object.getString("messageType"), "click")) {
                layout.buttonClicked(object.getString("nodeID"));
            }
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

    public static WebdashboardServer getInstance() {
        if (instance == null) {
            try {
                instance = new WebdashboardServer(port);
                RobotLog.v("dashboard server started");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}