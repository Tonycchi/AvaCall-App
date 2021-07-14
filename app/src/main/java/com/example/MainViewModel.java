package com.example;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.data.RobotModel;
import com.example.data.URLSettings;
import com.example.model.MainModel;
import com.example.model.connection.Device;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final MainModel model;
    private int[] modelPositionToId;

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

//    public MutableLiveData<String> getInviteLink() {
//        return model.getInviteLink();
//    }

    public String getShareURL() {
        return model.getShareURL();
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

    public void setReceiveCommands() { model.setReceiveCommands(); }

    public String[] getAllRobotNames() {
        List<RobotModel> allDBRobots = model.getAllRobots();
        int numberOfRobots = allDBRobots.size();

        String[] allRobotNames = new String[numberOfRobots];
        modelPositionToId = new int[numberOfRobots];

        for(int i=0; i<numberOfRobots; i++){
            RobotModel temp = allDBRobots.get(i);
            allRobotNames[i] = temp.name;
            modelPositionToId[i] = temp.id;
        }

        return allRobotNames;
    }


    public RobotModel getRobotModel(int modelPosition) {
        return model.getRobotModel(modelPositionToId[modelPosition]);
    }

    public void modelSelected(int modelPosition) {
        model.modelSelected(modelPositionToId[modelPosition]);
    }

    public void sendControlInput(int... input) {
        model.sendControlInputs(input);
    }

    public String getSelectedModelElements() {
        return model.getSelectedModelElements();
    }

    public RobotModel getCurrentRobotModel() {
        return model.getCurrentRobotModel();
    }
}
