package com.example.model.robot;

enum ControlElement {
    JOYSTICK(0, "joystick"), SLIDER(1, "slider"), BUTTON(2, "button");

    final int id;
    final String name;

    ControlElement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    static int getNr(String s) {
        for (ControlElement i: values()) {
            if (s.equals(i.name))
                return i.id;
        }
        return -1;
    }
}
