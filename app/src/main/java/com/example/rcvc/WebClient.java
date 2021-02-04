package com.example.rcvc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private JitsiRoom room;
    private AnalogController analogController;

    public WebClient(URI serverURI, JitsiRoom room, AnalogController analogController) {
        super(serverURI);
        this.room = room;
        this.analogController = analogController;
    }

    @Override
    /**
     * Sends a message when succesfully connected to the server
     */
    public void onOpen(ServerHandshake handshakeData) {
        send("app");
        send(room.url);
        send("https://meet.mintclub.org"); //TODO hardcoding entfernen
        Log.d(TAG,"new connection opened");
    }

    @Override
    /**
     * Sends a message when connection is closed
     */
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG,"closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        String[] values = new String[2];
        if (!(message.indexOf(";") == -1)) {
            values = message.split(";");
            analogController.sendPowers(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        Log.d(TAG,"received ByteBuffer");
    }

    @Override
    /**
     * Send an error log when an error occurs
     */
    public void onError(Exception ex) {
        Log.e(TAG,"an error occurred:" + ex);
    }
}