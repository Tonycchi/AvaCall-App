package com.example.model.robot;

public class ControllerInput {

    public final int angle, strength, slider1, slider2;

    public ControllerInput(int angle, int strength, int slider1, int slider2) {
        this.angle = angle;
        this.strength = strength;
        this.slider1 = slider1;
        this.slider2 = slider2;
    }
}
