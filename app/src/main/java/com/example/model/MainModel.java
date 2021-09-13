package com.example.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.Constants;
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

    private static final String TAG = "MainModel";

    private final RobotConnectionModel robotConnectionModel;
    private final Handshake handshake;
    private VideoConnectionModel videoConnectionModel;

    private final TestRobotModel testRobotModel;

    private final Robot robot;
    private Controller controller;

    // Model for ModelSelectionFragment
    private final ModelSelectionModel modelSelectionModel;
    private int[] modelPositionToId;

    private final LocalDatabase localDatabase;

    public MainModel(@NonNull Application application) {
        localDatabase = LocalDatabase.getInstance(application);

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        robot = new EV3(localDatabase.robotModelDAO()); //TODO: dont hard code

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        handshake = new EV3BluetoothHandshake();
        //handshake = new AcceptAllHandshake();
        robotConnectionModel = new BluetoothModel(localDatabase.connectedDeviceDAO(), handshake, this);

        testRobotModel = new TestRobotModel(this);

        //TODO: don't hard code robotType
        modelSelectionModel = new ModelSelectionModel(localDatabase.robotModelDAO(), Constants.TYPE_EV3);
        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
    }

    public Controller getController() {
        return controller;
    }

    public void setInputFromWebClient(boolean input) { controller.setInputFromWebClient(input); }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return robotConnectionModel.getConnectionStatus();
    }

    public void saveURLs(URLSettings.stringTriple urls) {
        videoConnectionModel.saveURLs(urls);
    }

    public String getID() {
        return videoConnectionModel.getID();
    }

    public void startConnection(Device device) {
        robotConnectionModel.startConnection(device);
        //controller = robot.getController(0, ((BluetoothModel) robotConnectionModel).getService());
    }

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    public boolean invitePartner() {
        videoConnectionModel.setController(controller);
        return videoConnectionModel.invitePartner();
    }

    public String getShareURL() {
        return videoConnectionModel.getShareURL();
    }

    public Object getOptions() {
        return videoConnectionModel.getOptions();
    }

    public void cancelRobotConnection() {
        robotConnectionModel.cancelConnection();
    }

    public void deviceAccepted() {
        robotConnectionModel.deviceAccepted();
    }

    public void setReceiveCommands(boolean receiveCommands) {
        videoConnectionModel.setReceiveCommands(receiveCommands);
    }

    public RobotModel getRobotModel(int position) {
        return modelSelectionModel.getRobotModel(position);
    }

    public void modelSelected(int position) { //this method is started when modell verwenden or steuerung bearbeiten is pressed
        if (position == -1) {
            controller = null;
            return;
        }
        modelSelectionModel.setSelectedModelPosition(position);
        RobotModel selectedRobotModel = modelSelectionModel.getRobotModel(position);
        controller = robot.getController(selectedRobotModel, robotConnectionModel.getService());
    }

    public void sendControlInputs(int... input) {
        controller.sendInput(input);
    }

    public void getControlOutputs() { controller.getOutput(); }

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

    public void setSelectedModelPosition(int position) {
        modelSelectionModel.setSelectedModelPosition(position);
    }

    public String[] getAllRobotNames() {
        return modelSelectionModel.getAllRobotNames();
    }


    public void receivedMessageFromRobot(byte[] message){
        Log.d(TAG, "received message length: " + message.length);
        int length = message[0];
        if(length == 7 && controller != null) {
            testRobotModel.receivedMotorStrengths(message);
        } else if(length == 5 && controller != null) {
            testRobotModel.checkStall(message);
        }
    }

    public void saveModel(int id, String name, String description, String type, List<List<Integer>> values) {
        RobotModel robotModel = robot.saveModel(id, name, description, type, values);
        //RobotModel selectedRobotModel = modelSelectionModel.getRobotModel(id);
        controller = robot.getController(robotModel, robotConnectionModel.getService()); // use model that was just saved
    }

    public String getCurrentRobotType() {
        if (robot != null) return robot.getType();
        throw new IllegalStateException();
    }

    public boolean isConnectedToServer() {
        return videoConnectionModel.isConnected();
    }

    public void cancelServerConnection() {
        videoConnectionModel.cancelConnection();
    }

    public void setLastUsedId(int id){controller.setLastUsedId(id);}

    public MutableLiveData<String> getMotorStrength() {
        return testRobotModel.getMotorStrength();
    }

    public MutableLiveData<Boolean> getStall(){return testRobotModel.getStall();}

    public void sendStallDetected(String controlElementType, int controlElementId) {
        videoConnectionModel.sendStallDetected(controlElementType, controlElementId);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        videoConnectionModel.sendStallEnded(controlElementType, controlElementId);
    }

    public MutableLiveData<Boolean> isVideoReady() {
        return videoConnectionModel.isVideoReady();
    }

    public void setImageOfSelectedModel(String photoPath) {
        modelSelectionModel.setImageOfSelectedModel(photoPath);
    }

    public void deleteModelByPosition(int position) {
        modelSelectionModel.deleteModelByPosition(position);
    }

    public void deleteModelById(int id) {
        modelSelectionModel.deleteModelById(id);
    }
}
