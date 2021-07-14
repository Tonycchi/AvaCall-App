package com.example.model;

import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;

import java.util.List;

public class ModelSelectionModel {

    private RobotModelDAO robotModelDAO;
    private String robotType;
    private int selectedModelPosition;

    public ModelSelectionModel(RobotModelDAO robotModelDAO, String robotType){
        this.robotModelDAO = robotModelDAO;
        this.robotType = robotType;
        selectedModelPosition = 0;
    }

    public List<RobotModel> getAllRobots() {
        return robotModelDAO.getAllModelsOfType(robotType);
    }

    public int getSelectedModelPosition(){
        return selectedModelPosition;
    }

    public RobotModel getRobotModel(int id) {
        return robotModelDAO.getRobotModel(id);
    }

    public void setSelectedModelPosition(int selectedModelPosition) {
        this.selectedModelPosition = selectedModelPosition;
    }
}
