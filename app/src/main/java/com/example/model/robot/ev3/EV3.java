package com.example.model.robot.ev3;

import com.example.data.RobotModelDAO;
import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;

import java.util.ArrayList;
import java.util.List;

public class EV3 implements Robot {

    private RobotModelDAO db;

    public EV3(RobotModelDAO db) {
        this.db = db;
    }

    @Override
    public Controller getController(int id, ConnectionService service) {
        return new EV3Controller(db.getSpecs(id), service);
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
