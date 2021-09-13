package com.example.model.connection;

import android.os.Parcelable;

public class Device {

    private Parcelable device;
    private final String name;

    public Device(String name) {
        this.name = name;
    }

    public Device(Parcelable device, String name) {
        this.device = device;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Parcelable getParcelable() {
        return device;
    }

}
