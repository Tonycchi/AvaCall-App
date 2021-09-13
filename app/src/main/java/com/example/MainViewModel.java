package com.example;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.data.RobotModel;
import com.example.data.URLSettings;
import com.example.model.MainModel;
import com.example.model.robotConnection.Device;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final MainModel model;

    public MainViewModel(@NonNull Application application) {
        super(application);
        model = new MainModel(application);
    }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return model.getPairedDevices();
    }

    public URLSettings.stringTriple getCurrentURLs() {
        return model.getVideoConnectionModel().getCurrentURLs();
    }

    public void saveURLs(URLSettings.stringTriple urls) {
        model.saveURLs(urls);
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return model.getConnectionStatus();
    }

    public void startConnection(Device device) {
        model.startConnection(device);
    }

    public boolean invitePartner() {
        return model.invitePartner();
    }

    public String getShareURL() {
        return model.getShareURL();
    }

    public String getID() {
        return model.getID();
    }

    public Object getOptions() {
        return model.getOptions();
    }

    public void cancelRobotConnection() {
        model.cancelRobotConnection();
    }

    public void deviceAccepted() {
        model.deviceAccepted();
    }

    public void setReceiveCommands(boolean receiveCommands) {
        model.setReceiveCommands(receiveCommands);
    }

    public String[] getAllRobotNames() {
        return model.getAllRobotNames();
    }

    public RobotModel getRobotModel(int modelPosition) {
        return model.getRobotModel(modelPosition);
    }

    public void modelSelected(int modelPosition) {
        model.modelSelected(modelPosition);
    }

    public String getCurrentRobotType() {
        return model.getCurrentRobotType();
    }

    public void sendControlInput(int... input) {
        model.sendControlInputs(input);
    }

    public void getControlOutput() { model.getControlOutputs(); }

    public String getSelectedModelElements() {
        return model.getSelectedModelElements();
    }

    public RobotModel getSelectedRobotModel() {
        return model.getSelectedRobotModel();
    }

    public int getSelectedModelPosition() {
        return model.getSelectedModelPosition();
    }

    public void setSelectedModelPosition(int position) {
        model.setSelectedModelPosition(position);
    }

    public void saveModel(int id, String name, String description, String type, List<List<Integer>> values) {
        model.saveModel(id, name, description, type, values);
    }

    public MutableLiveData<Boolean> isVideoReady() {
        return model.isVideoReady();
    }

    public boolean isConnectedToServer() {
        return model.isConnectedToServer();
    }

    public void cancelServerConnection() {
        model.cancelServerConnection();
    }

    public void setLastUsedId(int id){model.setLastUsedId(id);}

    public MutableLiveData<String> getMotorStrength() {
        return model.getMotorStrength();
    }

    public void deleteModelByPosition(int position) {
        model.deleteModelByPosition(position);
    }

    public void deleteModelById(int id) {
        model.deleteModelById(id);
    }

    public void setImageOfSelectedModel(String photoPath) {
        model.setImageOfSelectedModel(photoPath);
    }

    public MutableLiveData<Boolean> getStall() {return model.getStall();}

    public void sendStallDetected(String controlElementType, int controlElementId) {
        model.sendStallDetected(controlElementType, controlElementId);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        model.sendStallEnded(controlElementType, controlElementId);
    }

    public void setInputFromWebClient(boolean input) { model.setInputFromWebClient(input);}
}
