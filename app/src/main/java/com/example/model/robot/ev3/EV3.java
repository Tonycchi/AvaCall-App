package com.example.model.robot.ev3;

import com.example.Constants;
import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;
import com.example.model.robotConnection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@code EV3} implements {@code Robot} functionality specific to the LEGO MINDSTORM EV3.
 */
public class EV3 implements Robot {

    private final RobotModelDAO db;

    public EV3(RobotModelDAO db) {
        this.db = db;
    }

    @Override
    public Controller getController(RobotModel robotModel, ConnectionService service) {
        return new EV3Controller(robotModel, service);
    }

    public String getType() {
        return Constants.TYPE_EV3;
    }

    @Override
    public RobotModel saveModel(int id, String name, String description, String type, List<List<Integer>> values, String picture) {
        // values get sorted before saving:
        // joystick first, then slider, button
        // makes building UI on website/app easier
        Comparator<List<Integer>> c = (o1, o2) -> {
            if (o1.size() > 0 && o2.size() > 0) {
                return Integer.compare(o1.get(0), o2.get(0));
            }
            return 0;
        };
        Collections.sort(values, c);

        // create specification string
        StringBuilder specs = new StringBuilder();
        for (List<Integer> element : values) {
            switch (element.get(0)) {
                case 1: //Joystick
                    specs.append("joystick:")
                            .append(element.get(1)) // max power
                            .append(";").append(indexToPort(element.get(2))) // port right
                            .append(",").append(indexToPort(element.get(3))) // and left
                            .append("|");
                    break;
                case 2: //Slider
                    specs.append("slider:")
                            .append(element.get(1)) // max power
                            .append(";").append(indexToPort(element.get(2))) // port
                            .append("|");
                    break;
                case 3: //Button
                    specs.append("button:")
                            .append(element.get(1)) // max power
                            .append(";").append(indexToPort(element.get(2))) // port
                            .append(";").append(element.get(3)) // duration
                            .append("|");
                    break;
                default:
            }
        }
        // and save it
        if (specs.length() > 0) {
            specs = new StringBuilder(specs.substring(0, specs.length() - 1));
            RobotModel robotModel = new RobotModel(id, name, type, specs.toString(), description.trim(), picture);
            db.insertAll(robotModel);
            return robotModel;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * array index to port number <br>
     * 0 -> 1 <br>
     * 1 -> 2 <br>
     * 2 -> 4 <br>
     * 3 -> 8 <br>
     * @param index array index
     * @return port number
     */
    private int indexToPort(int index) {
        int y = 1, i = index;
        while (i > 0) {
            y = y << 1;
            i--;
        }
        return y;
    }
}
