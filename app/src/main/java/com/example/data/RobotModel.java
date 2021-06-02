package com.example.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RobotModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name, type, specs;

}
