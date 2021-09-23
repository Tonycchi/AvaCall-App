package com.example.model.robotConnection;

/**
 * {@code ConnectionService} provides a physical connection to a remote device.
 */
public interface ConnectionService {

    void write(byte[] bytes);
}
