package com.example.model.robot;

import com.example.data.RobotModel;
import com.example.model.robotConnection.ConnectionService;

import java.util.List;

/**
 * A {@code Robot} represents a physical robot model, i.e. a LEGO MINDSTORMS EV3 or LEGO MINDSTORMS
 * NXT. It provides the necessary {@code Controller} and a method for saving new models.
 */
public interface Robot {

    /**
     * @param robotModel used model
     * @return a controller using model settings specified by key
     */
    Controller getController(RobotModel robotModel, ConnectionService service);

    /**
     *
     * @return The robot's type/physical model
     */
    String getType();

    /**
     * Saves a model in database
     * @param id for db
     * @param name for db
     * @param description for db
     * @param type for db
     * @param values specifications
     * @param picture for db
     * @return A {@code RobotModel} representing the database entry created by this method.
     */
    RobotModel saveModel(int id, String name, String description, String type, List<List<Integer>> values, String picture);
}
