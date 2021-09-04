package com.example.model;

import android.net.Uri;

import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;

import java.util.List;

public class ModelSelectionModel {

    private RobotModelDAO robotModelDAO;
    private String robotType;
    private int selectedModelPosition;
    private int[] modelPositionToId;

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

    public RobotModel getRobotModel(int position) {
        return robotModelDAO.getRobotModel(modelPositionToId[position]);
    }

    public void setSelectedModelPosition(int selectedModelPosition) {
        this.selectedModelPosition = selectedModelPosition;
    }

    public void setImageOfSelectedModel(Uri imageUri) {
        robotModelDAO.setPictureOfRobotModel(modelPositionToId[selectedModelPosition], imageUri.toString());
    }

    public String[] getAllRobotNames() {
        List<RobotModel> allDBRobots = getAllRobots();
        int numberOfRobots = allDBRobots.size();

        String[] allRobotNames = new String[numberOfRobots];
        modelPositionToId = new int[numberOfRobots];

        for (int i = 0; i < numberOfRobots; i++) {
            RobotModel temp = allDBRobots.get(i);
            allRobotNames[i] = temp.name;
            modelPositionToId[i] = temp.id;
        }

        return allRobotNames;
    }
}
