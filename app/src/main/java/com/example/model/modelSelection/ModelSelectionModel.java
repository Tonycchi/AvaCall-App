package com.example.model.modelSelection;

import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;

import java.util.List;

public class ModelSelectionModel {

    private final RobotModelDAO robotModelDAO;
    private final String robotType;
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
        if(position >= modelPositionToId.length)
            return null;
        return robotModelDAO.getRobotModel(modelPositionToId[position]);
    }

    public void setSelectedModelPosition(int selectedModelPosition) {
        this.selectedModelPosition = selectedModelPosition;
    }

    public void setImageOfSelectedModel(String photoPath) {
        robotModelDAO.setPictureOfRobotModel(modelPositionToId[selectedModelPosition], photoPath);
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

    public void deleteModelByPosition(int position) {
        robotModelDAO.deleteByID(modelPositionToId[position]);
    }

    public void deleteModelById(int id) {
        robotModelDAO.deleteByID(id);
    }
}
