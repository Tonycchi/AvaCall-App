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
    public RobotModel saveModel(int id, String name, String description, String type, List<List<Integer>> values) {
        Comparator<List<Integer>> c = (o1, o2) -> {
            if (o1.size() > 0 && o2.size() > 0) {
                return Integer.compare(o1.get(0), o2.get(0));
            }
            return 0;
        };
        Collections.sort(values, c);

        String specs = "";
        for (List<Integer> element : values) {
            switch (element.get(0)) {
                case 1: //Joystick
                    specs += "joystick:" + element.get(1)
                            + ";" + indexToPort(element.get(2))
                            + "," + indexToPort(element.get(3)) + "|";
                    break;
                case 2: //Slider
                    specs += "slider:" + element.get(1)
                            + ";" + indexToPort(element.get(2)) + "|";
                    break;
                case 3: //Button
                    specs += "button:" + element.get(1)
                            + ";" + indexToPort(element.get(2))
                            + ";" + element.get(3) + "|";
                    break;
                default:
            }
        }
        if (specs.length() > 0) {
            specs = specs.substring(0, specs.length() - 1);
            RobotModel robotModel = new RobotModel(id, name, type, specs, description.trim());
            db.insertAll(robotModel);
            return robotModel;
        } else {
            throw new IllegalStateException();
        }
    }

    private int indexToPort(int index) {
        int y = 1, i = index;
        while (i > 0) {
            y = y << 1;
            i--;
        }
        return y;
    }
}
