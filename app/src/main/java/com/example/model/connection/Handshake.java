package com.example.model.connection;

public interface Handshake<T> {

    public T[] getSyn();
    public boolean isAckCorrect(T ack);

}
