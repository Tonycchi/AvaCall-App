package com.example.model.robot;

import java.util.List;

public interface Controller {

    /**
     * uses given controller input to send command to robot
     *
     * @param input input
     */
    void sendInput(int... input);

    List<Integer> getControlCounts();

}
