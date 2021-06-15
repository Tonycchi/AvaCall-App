package com.example.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ConnectedDevice.class, LocalPreference.class, RobotModel.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract ConnectedDeviceDAO connectedDeviceDAO();
    public abstract LocalPreferenceDAO localPreferenceDAO();
    public abstract RobotModelDAO robotModelDAO();
}
