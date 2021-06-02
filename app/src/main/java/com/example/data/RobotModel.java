package com.example.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RobotModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name, type, specs;

    public RobotModel(int id, String name, String type, String specs) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.specs = specs;
    }

}
