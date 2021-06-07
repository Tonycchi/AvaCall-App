package com.example.model.connection;

import java.util.List;

public interface Handshake<T> {

    public List<T> getSyn();
    public boolean isAckCorrect(T ack);

}
