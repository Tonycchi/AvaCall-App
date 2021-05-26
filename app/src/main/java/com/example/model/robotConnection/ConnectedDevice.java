package com.example.model.robotConnection;

import android.os.ParcelUuid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//is a Device that was previously connected with this app
@Entity
public class ConnectedDevice {

    @PrimaryKey
    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "lastConnected")
    private long lastConnected;

}
