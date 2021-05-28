package com.example.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//is a Device that was previously connected with this app
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

    public String getAddress() {
        return address;
    }

    public long getLastConnected() {
        return lastConnected;
    }

    @Override
    public String toString() {
        return "ConnectedDevice{" +
                "address='" + address + '\'' +
                ", lastConnected=" + (lastConnected - 1622100000000L) +
                '}';
    }
}
