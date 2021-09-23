package com.example.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A {@code ConnectedDevice} object represents a database entry for a bluetooth device that was
 * previously connected to this app.
 */
@Entity(tableName = "ConnectedDevice")
public class ConnectedDevice {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "lastConnected")
    public long lastConnected;

    public ConnectedDevice(String address, long lastConnected) {
        this.address = address;
        this.lastConnected = lastConnected;
    }

    @Override
    public String toString() {
        return "ConnectedDevice{" +
                "address='" + address + '\'' +
                ", lastConnected=" + (lastConnected - 1622100000000L) +
                '}';
    }
}
