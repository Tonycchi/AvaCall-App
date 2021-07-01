package com.example.model;

import android.util.Log;

import com.example.model.robot.Controller;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private final String jitsi;
    private Controller controller;
    private String id;
    private boolean receiveCommands;
    private boolean ready;

    public WebClient(URI serverURI, String jitsi, Controller controller) {
        super(serverURI);
        this.controller = controller;
        this.jitsi = jitsi;
        this.ready = false;
        receiveCommands = false;
        Log.d(TAG, "serverURI:" + serverURI.toASCIIString());
    }

    /**
     * Sends a message when successfully connected to the server
     */
    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Log.d(TAG, "open");
        String t = controller.getControlElementString();
        send("app:" + jitsi + t);
        Log.d(TAG, t);
    }

    /**
     * Sends a message when connection is closed
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "closed with exit code " + code + " additional info: " + reason);
        ready = false;
    }

    /**
     * Handles getting an input message from the WebClient
     *
     * @param message the input message
     */
    @Override
    public void onMessage(String message) {
        Log.d(TAG, message);
        if (message.startsWith("id:")) {
            id = message.split(":", 2)[1];
            ready = true;
        } else {
            if (true) {
                List<String> t1 = Arrays.asList(message.split(";|:"));
                int[] t2 = new int[t1.size()];
                for (int i = 0; i < t2.length; i++)
                    t2[i] = Integer.parseInt(t1.get(i));

                controller.sendInput(t2);
                //controller.send(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
            }
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
    }

    public boolean isReady() {
        return ready;
    }

    /**
     * Send an error log when an error occurs
     */
    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "an error occurred:" + ex);
    }

    public String getId() {
        return id;
    }

    public void setReceiveCommands() {
        receiveCommands = true;
    }
}