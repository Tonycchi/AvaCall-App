package com.example.data;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RobotModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name; //the name of the robot, that is shown in the list

    @ColumnInfo(name = "type")
    public String type; //the type of the robot e.g.: ev3

    @ColumnInfo(name = "specs")
    public String specs; //the specs of the robot, translated to a string

    @ColumnInfo(name = "description")
    public String description; //the description of the robot, can be null. If null, in ui specs are shown

    @ColumnInfo(name = "picture")
    public String picture; //uri to the image

    /**
     *
     * @param id 0 for new model, >0 to overwrite an existing model
     * @param name will be displayed in model selection
     * @param type type of robot
     * @param specs specifies which control elements map to which motors
     */
    public RobotModel(int id, String name, String type, String specs,  String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.specs = specs;
        this.description = description;
        picture = null;
    }

}
