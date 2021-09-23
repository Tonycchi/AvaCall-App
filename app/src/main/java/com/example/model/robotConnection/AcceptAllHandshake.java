package com.example.model.robotConnection;

import com.example.model.robotConnection.Handshake;

import java.util.ArrayList;
import java.util.List;

public class AcceptAllHandshake implements Handshake {

    @Override
    public List<byte[]> getSyn() {
        List<byte[]> temp = new ArrayList<byte[]>();
        return temp;
    }

    @Override
    public boolean isAckCorrect(Object ack) {
        return true;
    }
}