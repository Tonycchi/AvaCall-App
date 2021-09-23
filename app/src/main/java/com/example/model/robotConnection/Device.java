package com.example.model.robotConnection;

import android.os.Parcelable;

/**
 * {@code Device} represents a device which one can connect with. It contains the necessary data for
 * opening a connection and to display the device in a list.
 */
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
