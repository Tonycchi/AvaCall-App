package com.example.model.connection;

public interface ConnectionService {

    void write(byte[] bytes);

    void setIsStallThread(boolean stallThread);
}
