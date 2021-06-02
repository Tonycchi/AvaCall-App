package com.example.model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.data.ConnectedDevice;
import com.example.data.LocalDatabase;
import com.example.data.RobotModel;
import com.example.data.URLFactory;
import com.example.model.connection.BluetoothModel;
import com.example.model.connection.Device;
import com.example.model.connection.RobotConnectionModel;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;
import com.example.model.robot.ev3.EV3;

import java.util.ArrayList;

public class MainModel {

    private Context context;

    private RobotConnectionModel robotConnectionModel;

    private VideoConnectionModel videoConnectionModel;

    private Robot robot;
    private Controller controller;

    // Model for ModelSelectionFragment
    // TODO modelle abspeichern?

    // Model for EditControlsFragment
    // TODO Liste von eigener controller klasse???????

    // Model for VideoConnectionFragment
    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;
    private LocalDatabase localDatabase;


    public MainModel(@NonNull Application application) {
        localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database").allowMainThreadQueries().build();

        robot = new EV3(localDatabase.robotModelDAO());

        localDatabase.robotModelDAO().insertAll(new RobotModel(99,"test", "EV3", "A;D"));

        videoConnectionModel = new VideoConnectionModel(PreferenceManager.getDefaultSharedPreferences(application));
        robotConnectionModel = new BluetoothModel(localDatabase.connectedDeviceDAO());
    }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return robotConnectionModel.getConnectionStatus();
    }

    public void startConnection(Device device) {
        robotConnectionModel.startConnection(device);
        controller = robot.getController(99, ((BluetoothModel) robotConnectionModel).getService());
    }

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    public void invitePartner() {
        videoConnectionModel.invitePartner();
        videoConnectionModel.setController(controller);
    }

    public MutableLiveData<String> getInviteLink() {
        return videoConnectionModel.getInviteLink();
    }

    public SessionData getSession() {
        return videoConnectionModel.getSession();
    }

    public void connectingCanceled() {
        robotConnectionModel.connectingCanceled();
    }
}
