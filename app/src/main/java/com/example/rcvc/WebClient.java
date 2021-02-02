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
    private Context context;
    private JitsiRoom room;

    public WebClient(URI serverURI, Context context, JitsiRoom room) {
        super(serverURI);
        this.context = context;
        this.room = room;
    }

    @Override
    /**
     * Sends a message when succesfully connected to the server
     */
    public void onOpen(ServerHandshake handshakeData) {
        send("app");
        send(room.url);
        send("https://meet.mintclub.org");
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
        }
        Intent intent = new Intent(context.getString(R.string.action_controller));
        intent.putExtra("values", values);
        context.sendBroadcast(intent);
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