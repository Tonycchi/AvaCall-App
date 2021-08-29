package com.example;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.data.RobotModel;
import com.example.data.URLSettings;
import com.example.model.MainModel;
import com.example.model.connection.Device;

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

    public void invitePartner() {
        model.invitePartner();
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

    public void cancelConnection() {
        model.cancelConnection();
    }

    public void deviceAccepted() {
        model.deviceAccepted();
    }

    public void setReceiveCommands() {
        model.setReceiveCommands();
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

    public void deleteModel(int id) {
        model.deleteModel(id);
    }
}
