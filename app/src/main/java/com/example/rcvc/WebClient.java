package com.example.rcvc;

import android.util.Log;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private AnalogController analogController;
    private String id;
    private final String jitsi;
    private boolean receiveCommands;
    private MainActivity mainActivity;
    private boolean ready;

    public WebClient(URI serverURI, String jitsi, AnalogController analogController, MainActivity mainActivity) {
        super(serverURI);
        this.analogController = analogController;
        this.jitsi = jitsi;
        this.mainActivity = mainActivity;
        this.ready = false;
        //as long as this is not correctly implemented let this on true
        receiveCommands = true;
    }

    @Override
    /**
     * Sends a message when succesfully connected to the server
     */
    public void onOpen(ServerHandshake handshakeData) {
        send("app:" + jitsi);
        Log.d(TAG,"new connection opened");
    }

    @Override
    /**
     * Sends a message when connection is closed
     */
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG,"closed with exit code " + code + " additional info: " + reason);
        ready = false;
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, message);
        if (message.startsWith("id:")) {
            id = message.split(":",2)[1];
            ready = true;
        } else {
            if (receiveCommands) {
                String[] values = new String[2];
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

    @Override
    /**
     * Send an error log when an error occurs
     */
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