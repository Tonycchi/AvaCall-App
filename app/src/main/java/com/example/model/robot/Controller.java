package com.example.model.robot;

import com.example.data.RobotModel;

/**
 * A {@code Controller} sends commands to a connected robot. The implementation is specific to the
 * type of robot. It uses {@code RobotModel} to determine mappings between UI inputs and sent
 * commands.
 */
public interface Controller {

    /**
     * uses given controller input to send command to robot
     *
     * @param input input
     */
    void sendInput(int... input);

    /**
     * get the output for the motors from the robot
     */
    void getOutput();

    /**
     * 
     * @return String specifying which control elements to show on website
     */
    String getControlElementString();

    /**
     *
     * @return Object representing used model
     */
    RobotModel getCurrentModel();

    /**
     * Sets ID of last control element used by the {@code Controller}
     * @param id ID
     */
    void setLastUsedId(int id);

    /**
     * @return id ID of last control element used by the {@code Controller}
     */
    int getLastUsedId();

    /**
     * Specify if input comes from a web client
     * @param input true -> from web client,<br>&emsp;&emsp; false -> from app
     */
    void setInputFromWebClient(boolean input);

}
