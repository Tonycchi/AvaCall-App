package com.example.model.robotConnection;

import java.util.List;

public interface Handshake<T> {

    List<T> getSyn();
    boolean isAckCorrect(T ack);

}
