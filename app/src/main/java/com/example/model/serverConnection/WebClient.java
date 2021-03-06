package com.example.model.serverConnection;

import android.util.Log;

import com.example.model.robot.Controller;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Manages a WebSocket connection to an AvaCall server. Opens a connections and sends/receives data.
 */
public class WebClient extends WebSocketClient {

    private final String TAG = "WebClient";
    private final String videoURL;
    private final Controller controller;
    private String id;
    private boolean receiveCommands;
    /**
     * Connection status <br>
     * -1: error <br>
     * 0: no connection <br>
     * 1: connected
     */
    private int status;

    public WebClient(URI serverURI, String videoURL, Controller controller) {
        super(serverURI);
        this.controller = controller;
        this.videoURL = videoURL;
        this.status = 0;
        receiveCommands = false;
        Log.d(TAG, "serverURI:" + serverURI.toASCIIString());
    }

    /**
     * Sends a message when successfully connected to the server
     */
    @Override
    public void onOpen(ServerHandshake handshakeData) {
        String syn = "app:" + videoURL + ":" + controller.getControlElementString();
        send(syn);
        Log.d(TAG, "send handshake:" + syn);
    }

    /**
     * Sends a message when connection is closed
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "closed with exit code " + code + " additional info: " + reason);
        status = 0;
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
            status = 1;
            // once we receive an ID we are connected
        } else {
            if (receiveCommands) {
                // parse received string
                List<String> t1 = Arrays.asList(message.split(";|:"));
                int[] receivedVals = new int[t1.size()];
                for (int i = 0; i < receivedVals.length; i++)
                    receivedVals[i] = Integer.parseInt(t1.get(i));
                // for stall detection
                controller.setLastUsedId(receivedVals[0]);
                controller.setInputFromWebClient(true);
                // send to controller
                Thread webClientInput = new Thread() {
                    public void run() {
                        controller.sendInput(receivedVals);
                    }
                };
                webClientInput.start();
                //controller.send(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
            }
        }
    }

    public void sendStallDetected(String controlElementType, int controlElementId) {
        String stallMessage = "STALL:start:" + controlElementType + ":" + controlElementId;
        send(stallMessage);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        String stallMessage = "STALL:stop:" + controlElementType + ":" + controlElementId;
        send(stallMessage);
    }


    @Override
    public void onMessage(ByteBuffer message) {
    }

    public int getStatus() {
        return status;
    }

    /**
     * Send an error log when an error occurs
     */
    @Override
    public void onError(Exception ex) {
        Log.e(TAG, ex + ex.getMessage());
        status = -1;
    }

    public String getId() {
        return id;
    }

    public void setReceiveCommands(boolean receiveCommands) {
        this.receiveCommands = receiveCommands;
    }
}