package com.example.model.robotConnection;

import java.util.List;

/**
 * A {@code Handshake} is used to determine if a connected device is of a given type. Which type and
 * how to determine it is specified in its implementations.
 * @param <T> Type of data sent/received by a device.
 */
public interface Handshake<T> {

    List<T> getSyn();
    boolean isAckCorrect(T ack);

}
