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

/**
 * View model in MVVM. As an extension of an android native class it separates model data from the
 * fragments' and activity's lifecycles.
 */
public class MainViewModel extends AndroidViewModel {

    private final MainModel model;

    public MainViewModel(@NonNull Application application) {
        super(application);
        model = new MainModel(application);
    }

    // BLUETOOTH

    /**
     *
     * @return devices paired with this phone
     */
    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return model.getPairedDevices();
    }

    /**
     * @return possible values: <br>
     * {@code BluetoothConnectionService.NOT_TESTED}, <br>
     * {@code BluetoothConnectionService.CONNECTED}, <br>
     * {@code BluetoothConnectionService.COULD_NOT_CONNECT}, <br>
     * {@code BluetoothConnectionService.CONNECTION_LOST},  <br>
     * {@code BluetoothConnectionService.CONNECTION_ACCEPTED}, <br>
     * {@code BluetoothConnectionService.CONNECTION_NOT_ACCEPTED}
     */
    public MutableLiveData<Integer> getConnectionStatus() {
        return model.getConnectionStatus();
    }

    /**
     * Start (bluetooth) connection w/ device
     * @param device device to connect to
     */
    public void startDeviceConnection(Device device) {
        model.startConnection(device);
    }

    /**
     * Cancels current (bluetooth) connection to robot
     */
    public void cancelRobotConnection() {
        model.cancelRobotConnection();
    }

    public void deviceAccepted() {
        model.deviceAccepted();
    }

    // VIDEO/SERVER CONNECTION

    /**
     *
     * @return URLs and internet port specified in URL settings
     */
    public URLSettings.StringTriple getCurrentURLs() {
        return model.getVideoConnectionModel().getCurrentURLs();
    }

    /**
     * Save specified URL settings in database.
     *
     * @param urls Three strings
     */
    public void saveURLs(URLSettings.StringTriple urls) {
        model.saveURLs(urls);
    }

    /**
     * start process of requesting a link from server
     * @return {@code true} if successful
     */
    public boolean invitePartner() {
        return model.invitePartner();
    }

    /**
     * @return link to be shared with call partner
     */
    public String getShareURL() {
        return model.getShareURL();
    }

    /**
     * @return ID of video call room
     */
    public String getID() {
        return model.getID();
    }

    /**
     * @return options necessary to start video call UI instance
     */
    public Object getVideoCallOptions() {
        return model.getVideoCallOptions();
    }

    /**
     * Sets, whether WebClient can receive commands and pass them to Controller
     * @param receiveCommands true if ready
     */
    public void setReceiveCommands(boolean receiveCommands) {
        model.setReceiveCommands(receiveCommands);
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

    // MODEL SELECTION & EDITING

    /**
     * @return Names of saved robot models
     */
    public String[] getAllModelNames() {
        return model.getAllModelNames();
    }

    /**
     * @param modelPosition position in list (as seen in model selection screen)
     * @return {@code RobotModel} at specified model
     */
    public RobotModel getRobotModel(int modelPosition) {
        return model.getRobotModel(modelPosition);
    }

    /**
     * Selects model at {@code modelPosition}
     * @param modelPosition position in list (as seen in model selection screen)
     */
    public void modelSelected(int modelPosition) {
        model.modelSelected(modelPosition);
    }

    public String getCurrentRobotType() {
        return model.getCurrentRobotType();
    }

    public void saveModel(int id, String name, String description, String type, List<List<Integer>> values) {
        model.saveModel(id, name, description, type, values);
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

    // NEEDED FOR STALL DETECTION

    public void sendControlInput(int... input) {
        model.sendControlInputs(input);
    }

    public void getControlOutput() {
        model.getControlOutputs();
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

    public void setLastUsedId(int id) {
        model.setLastUsedId(id);
    }

    public MutableLiveData<String> getMotorStrength() {
        return model.getMotorStrength();
    }

    public MutableLiveData<Boolean> getStall() {
        return model.getStall();
    }

    public void sendStallDetected(String controlElementType, int controlElementId) {
        model.sendStallDetected(controlElementType, controlElementId);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        model.sendStallEnded(controlElementType, controlElementId);
    }

    public void setInputFromWebClient(boolean input) {
        model.setInputFromWebClient(input);
    }
}
