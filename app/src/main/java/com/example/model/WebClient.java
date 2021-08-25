package com.example.model;

import android.util.Log;

import com.example.model.robot.Controller;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private final String videoURL;
    private Controller controller;
    private String id;
    private boolean receiveCommands;
    private boolean ready, error;

    public WebClient(URI serverURI, String videoURL, Controller controller) {
        super(serverURI);
        this.controller = controller;
        this.videoURL = videoURL;
        this.ready = false;
        receiveCommands = false;
        error = false;
        Log.d(TAG, "serverURI:" + serverURI.toASCIIString());
    }

    /**
     * Sends a message when successfully connected to the server
     */
    @Override
    public void onOpen(ServerHandshake handshakeData) {
        String t = controller.getControlElementString();
        send("app:" + videoURL + ":" + t);
        Log.d(TAG, "open, control elements: " + t);
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

    public boolean isReady() throws ConnectException {
        if (error) throw new ConnectException();
        return ready;
    }

    /**
     * Send an error log when an error occurs
     */
    @Override
    public void onError(Exception ex) {
        error = true;
        Log.e(TAG, "an error occurred:" + ex);
    }

    public String getId() {
        return id;
    }

    public void setReceiveCommands() {
        receiveCommands = true;
    }
}