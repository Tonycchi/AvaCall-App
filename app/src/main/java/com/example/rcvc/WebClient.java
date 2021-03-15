package com.example.rcvc;

import android.util.Log;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private Controller analogController;
    private String id;
    private final String jitsi;
    private boolean receiveCommands;
    private boolean ready;

    public WebClient(URI serverURI, String jitsi, Controller analogController) {
        super(serverURI);
        this.analogController = analogController;
        this.jitsi = jitsi;
        this.ready = false;
        receiveCommands = false;
    }

    /**
     * Sends a message when successfully connected to the server
     */
    @Override
    public void onOpen(ServerHandshake handshakeData) {
        send("app:" + jitsi);
        Log.d(TAG, jitsi);
        Log.d(TAG,"new connection opened");
    }

    /**
     * Sends a message when connection is closed
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG,"closed with exit code " + code + " additional info: " + reason);
        ready = false;
    }

    /**
     * Handles getting an input message from the WebClient
     * @param message the input message
     */
    @Override
    public void onMessage(String message) {
        Log.d(TAG, message);
        if (message.startsWith("id:")) {
            id = message.split(":",2)[1];
            ready = true;
        } else {
            if (receiveCommands) {
                String[] values;
                if (message.contains(";")) {
                    values = message.split(";", 2);
                    analogController.sendPowers(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
                }
            }
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        Log.d(TAG,"received ByteBuffer");
    }

    public boolean isReady(){
        return ready;
    }

    /**
     * Send an error log when an error occurs
     */
    @Override
    public void onError(Exception ex) {
        Log.e(TAG,"an error occurred:" + ex);
    }

    public String getId() {
        return id;
    }

    public void setReceiveCommands() {
        receiveCommands = true;
    }
}