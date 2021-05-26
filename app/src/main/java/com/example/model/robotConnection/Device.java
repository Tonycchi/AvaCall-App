package com.example.model.robotConnection;

import android.os.Parcelable;

public class Device {

    private Parcelable device;
    private String name;

    public Device (String name){
        this.name = name;
    }

    public Device(Parcelable device, String name){
        this.device = device;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Parcelable getParcelable(){
        return device;
    }

}