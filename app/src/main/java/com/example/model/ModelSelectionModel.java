package com.example.model;

import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;

import java.util.List;

public class ModelSelectionModel {

    private RobotModelDAO robotModelDAO;
    private String robotType;

    public ModelSelectionModel(RobotModelDAO robotModelDAO, String robotType){
        this.robotModelDAO = robotModelDAO;
        this.robotType = robotType;
    }

    public RobotModel[] getAllRobots() {
        return (RobotModel[])robotModelDAO.getAllModelsOfType(robotType).toArray();
    }
}
