package com.example.model.robot.ev3;

import com.example.Constants;
import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;
import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;

import java.util.List;

public class EV3 implements Robot {

    private RobotModelDAO db;

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
    public void saveModel(int id, String name, String description, String type, List<List<Integer>> values) {
        String specs = "";
        for (List<Integer> element : values) {
            switch (element.get(0)) { // TODO define numbers centrally, maybe in Constants.java alongside robot type strings
                case Constants.JOYSTICK: //Joystick
                    specs += "joystick:" + element.get(1)
                            + ";" + indexToPort(element.get(2))
                            + "," + indexToPort(element.get(3)) + "|";
                    break;
                case Constants.SLIDER: //Slider
                    specs += "slider:" + element.get(1)
                            + ";" + indexToPort(element.get(2)) + "|";
                    break;
                case Constants.BUTTON: //Button
                    specs += "button:" + element.get(1)
                            + ";" + indexToPort(element.get(2))
                            + ";" + element.get(3) + "|";
                    break;
                default:
            }
        }
        if (specs.length() > 0) {
            specs = specs.substring(0, specs.length() - 1);
            db.insertAll(new RobotModel(id, name, type, specs, description.trim()));
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

    /*private List<List<Integer>> specsToArray(String specs) {
        List<List<Integer>> r = new ArrayList<>();

        String[] controlElements = specs.split("\\|");
        for (String element : controlElements) {
            List<Integer> e = new ArrayList<>();
            String[] tmp = element.split(":");
            String[] attrs = tmp[1].split(";");

            e.add(EV3ControlElement.ID.getNr(tmp[0]));
            for (String a : attrs)
                e.add(Integer.parseInt(a));

            r.add(e);
        }

        return r;
    }

     */
}
