package com.example.model;

import static com.example.Constants.TYPE_EV3;
import static com.example.Constants.USED_MODEL_TYPE;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.data.LocalDatabase;
import com.example.data.RobotModel;
import com.example.data.URLSettings;
import com.example.model.modelSelection.ModelSelectionModel;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;
import com.example.model.robot.ev3.EV3;
import com.example.model.robotConnection.BluetoothModel;
import com.example.model.robotConnection.Device;
import com.example.model.robotConnection.EV3BluetoothHandshake;
import com.example.model.robotConnection.Handshake;
import com.example.model.robotConnection.RobotConnectionModel;
import com.example.model.serverConnection.VideoConnectionModel;
import com.example.model.testRobot.TestRobotModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MVVM-model for the entire state of the app. Contains all other MVVM-models and methods for
 * interacting with those.
 */
public class MainModel {

    private static final String TAG = "MainModel";

    private final RobotConnectionModel robotConnectionModel;
    private final Handshake handshake;
    private final VideoConnectionModel videoConnectionModel;

    private final TestRobotModel testRobotModel;

    private final Robot robot;
    // Model for ModelSelectionFragment
    private final ModelSelectionModel modelSelectionModel;
    private final LocalDatabase localDatabase;
    private Controller controller;

    public MainModel(@NonNull Application application) {
        localDatabase = LocalDatabase.getInstance(application);

        switch (USED_MODEL_TYPE) {
            case TYPE_EV3:
                robot = new EV3(localDatabase.robotModelDAO());
                break;
            default:
                robot = null;
                Log.e(TAG, "No model type");
        }

        handshake = new EV3BluetoothHandshake();
        //handshake = new AcceptAllHandshake();
        robotConnectionModel = new BluetoothModel(localDatabase.connectedDeviceDAO(), handshake, this);

        testRobotModel = new TestRobotModel(this);

        modelSelectionModel = new ModelSelectionModel(localDatabase.robotModelDAO(), USED_MODEL_TYPE);
        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
    }

    /**
     *
     * @return devices paired with this phone
     */
    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    // BLUETOOTH

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
        return robotConnectionModel.getConnectionStatus();
    }

    /**
     * Start (bluetooth) connection w/ device
     * @param device device to connect to
     */
    public void startDeviceConnection(Device device) {
        robotConnectionModel.startConnection(device);
        //controller = robot.getController(0, ((BluetoothModel) robotConnectionModel).getService());
    }

    /**
     * Cancels current (bluetooth) connection to robot
     */
    public void cancelRobotConnection() {
        robotConnectionModel.cancelConnection();
    }

    public void deviceAccepted() {
        robotConnectionModel.deviceAccepted();
    }

    // VIDEO/SERVER CONNECTION

    /**
     * Save specified URL settings in database.
     *
     * @param urls Three strings
     */
    public void saveURLs(URLSettings.StringTriple urls) {
        videoConnectionModel.saveURLs(urls);
    }

    /**
     * start process of requesting a link from server
     * @return {@code true} if successful
     */
    public boolean invitePartner() {
        videoConnectionModel.setController(controller);
        return videoConnectionModel.invitePartner();
    }

    /**
     * @return link to be shared with call partner
     */
    public String getShareURL() {
        return videoConnectionModel.getShareURL();
    }

    /**
     * @return ID of video call room
     */
    public String getID() {
        return videoConnectionModel.getID();
    }

    /**
     * @return options necessary to start video call UI instance
     */
    public Object getVideoCallOptions() {
        return videoConnectionModel.getOptions();
    }

    /**
     * Sets, whether WebClient can receive commands and pass them to Controller
     * @param receiveCommands true if ready
     */
    public void setReceiveCommands(boolean receiveCommands) {
        videoConnectionModel.setReceiveCommands(receiveCommands);
    }

    public MutableLiveData<Boolean> isVideoReady() {
        return videoConnectionModel.isVideoReady();
    }

    public boolean isConnectedToServer() {
        return videoConnectionModel.isConnected();
    }

    public void cancelServerConnection() {
        videoConnectionModel.cancelConnection();
    }

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    /**
     *
     * @return currently set {@code Controller}
     */
    public Controller getController() {
        return controller;
    }

    // MODEL SELECTION & EDITING

    /**
     * @return Names of saved robot models
     */
    public String[] getAllModelNames() {
        return modelSelectionModel.getAllRobotNames();
    }

    /**
     * @param modelPosition position in list (as seen in model selection screen)
     * @return {@code RobotModel} at specified model
     */
    public RobotModel getRobotModel(int modelPosition) {
        return modelSelectionModel.getRobotModel(modelPosition);
    }

    /**
     * Selects model at {@code modelPosition}
     * @param modelPosition position in list (as seen in model selection screen)
     */
    public void modelSelected(int modelPosition) { //this method is started when modell verwenden or steuerung bearbeiten is pressed
        if (modelPosition == -1) {
            controller = null;
            return;
        }
        modelSelectionModel.setSelectedModelPosition(modelPosition);
        RobotModel selectedRobotModel = modelSelectionModel.getRobotModel(modelPosition);
        controller = robot.getController(selectedRobotModel, robotConnectionModel.getService());
    }

    public String getCurrentRobotType() {
        if (robot != null) return robot.getType();
        throw new IllegalStateException();
    }

    public void saveModel(int id, String name, String description, String type, List<List<Integer>> values, String picture) {
        RobotModel robotModel = robot.saveModel(id, name, description, type, values, picture);
        //RobotModel selectedRobotModel = modelSelectionModel.getRobotModel(id);
        controller = robot.getController(robotModel, robotConnectionModel.getService()); // use model that was just saved
    }

    public void deleteModelByPosition(int position) {
        modelSelectionModel.deleteModelByPosition(position);
    }

    public void deleteModelById(int id) {
        modelSelectionModel.deleteModelById(id);
    }

    public void setImageOfSelectedModel(String photoPath) {
        modelSelectionModel.setImageOfSelectedModel(photoPath);
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

    // NEEDED FOR STALL DETECTION

    public void setInputFromWebClient(boolean input) {
        controller.setInputFromWebClient(input);
    }

    public void sendControlInputs(int... input) {
        controller.sendInput(input);
    }

    public void getControlOutputs() {
        controller.getOutput();
    }

    public String getSelectedModelElements() {
        return controller.getControlElementString();
    }

    public void receivedMessageFromRobot(byte[] message) {
        Log.d(TAG, "received message length: " + message.length);
        int length = message[0];
        if (length == 7 && controller != null) {
            testRobotModel.receivedMotorStrengths(message);
        } else if (length == 5 && controller != null) {
            testRobotModel.checkStall(message);
        }
    }

    public void setLastUsedId(int id) {
        controller.setLastUsedId(id);
    }

    public MutableLiveData<String> getMotorStrength() {
        return testRobotModel.getMotorStrength();
    }

    public MutableLiveData<Boolean> getStall() {
        return testRobotModel.getStall();
    }

    public void sendStallDetected(String controlElementType, int controlElementId) {
        videoConnectionModel.sendStallDetected(controlElementType, controlElementId);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        videoConnectionModel.sendStallEnded(controlElementType, controlElementId);
    }
}
