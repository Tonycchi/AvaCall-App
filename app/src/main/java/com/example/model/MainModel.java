package com.example.model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.data.LocalDatabase;
import com.example.data.URLSettings;
import com.example.data.RobotModel;
import com.example.data.URLSettings;
import com.example.model.connection.BluetoothModel;
import com.example.model.connection.Device;
import com.example.model.connection.EV3BluetoothHandshake;
import com.example.model.connection.Handshake;
import com.example.model.connection.RobotConnectionModel;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;
import com.example.model.robot.ev3.EV3;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.util.ArrayList;
import java.util.List;

public class MainModel {

    private Context context;

    private RobotConnectionModel robotConnectionModel;
    private Handshake handshake;
    private VideoConnectionModel videoConnectionModel;

    private Robot robot;
    private Controller controller;

    // Model for ModelSelectionFragment
    private ModelSelectionModel modelSelectionModel;
    private int[] modelPositionToId;

    private LocalDatabase localDatabase;

    public MainModel(@NonNull Application application) {
        localDatabase = LocalDatabase.getInstance(application);

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        robot = new EV3(localDatabase.robotModelDAO());

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        handshake = new EV3BluetoothHandshake();
        //handshake = new AcceptAllHandshake();
        robotConnectionModel = new BluetoothModel(localDatabase.connectedDeviceDAO(), handshake);
        //TODO: don't hard code robotType
        modelSelectionModel = new ModelSelectionModel(localDatabase.robotModelDAO(), "EV3");
        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
    }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return robotConnectionModel.getConnectionStatus();
    }

    public void saveURLs(URLSettings.stringTriple urls) {
        videoConnectionModel.saveURLs(urls);
    }

    public void startConnection(Device device) {
        robotConnectionModel.startConnection(device);
        //controller = robot.getController(0, ((BluetoothModel) robotConnectionModel).getService());
    }

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    public void invitePartner() {
        videoConnectionModel.setController(controller);
        videoConnectionModel.invitePartner();
    }

    public String getShareURL() {
        return videoConnectionModel.getShareURL();
    }

    public Object getOptions() {
        return videoConnectionModel.getOptions();
    }

    public void cancelConnection() {
        robotConnectionModel.cancelConnection();
    }

    public void deviceAccepted() {
        robotConnectionModel.deviceAccepted();
    }

    public void setReceiveCommands() {
        videoConnectionModel.setReceiveCommands();
    }

    public RobotModel getRobotModel(int id) {
        return modelSelectionModel.getRobotModel(id);
    }

    public void modelSelected(int id) { //this method is started when modell verwenden or steuerung bearbeiten is pressed
        RobotModel selectedRobotModel = modelSelectionModel.getRobotModel(id);
        //TODO: create robot
        controller = robot.getController(id, robotConnectionModel.getService());
    }

    public void sendControlInputs(int... input) {
        controller.sendInput(input);
    }

    public String getSelectedModelElements() {
        return controller.getControlElementString();
    }

    /**
     * @return currently selected model, null if none selected
     */
    public RobotModel getSelectedRobotModel() {
        if (controller != null)
            return controller.getCurrentModel();
        return null;
    }

    public int getSelectedModelPosition() {
        return modelSelectionModel.getSelectedModelPosition();
    }

    public String[] getAllRobotNames() {
        List<RobotModel> allDBRobots = modelSelectionModel.getAllRobots();
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

    public void saveModel(int id, String name, String type, List<Integer[]> values) {
        robot.saveModel(id, name, type, values);
        controller = robot.getController(id, robotConnectionModel.getService());
    }
}
