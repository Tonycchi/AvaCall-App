package com.example.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.model.robotConnection.ConnectedDevice;
import com.example.model.robotConnection.ConnectedDeviceDAO;

@Database(entities = {ConnectedDevice.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract ConnectedDeviceDAO connectedDeviceDAO();
}
