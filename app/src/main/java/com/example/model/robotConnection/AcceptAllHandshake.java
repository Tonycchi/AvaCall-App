package com.example.model.robotConnection;

import java.util.ArrayList;
import java.util.List;

public class AcceptAllHandshake implements ByteArrayHandshake{

    @Override
    public List<byte[]> getSyn() {
        List<byte[]> temp = new ArrayList<byte[]>();
        return temp;
    }

    @Override
    public boolean isAckCorrect(byte[] ack) {
        return true;
    }
}
