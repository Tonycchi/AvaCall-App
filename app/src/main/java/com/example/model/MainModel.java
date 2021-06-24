package com.example.model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.data.ConnectedDevice;
import com.example.data.LocalDatabase;
import com.example.data.URLSettings;
import com.example.data.RobotModel;
import com.example.model.connection.AcceptAllHandshake;
import com.example.model.connection.BluetoothModel;
import com.example.model.connection.Device;
import com.example.model.connection.EV3BluetoothHandshake;
import com.example.model.connection.Handshake;
import com.example.model.connection.RobotConnectionModel;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;
import com.example.model.robot.ev3.EV3;
import com.example.ui.ModelSelectionFragment;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.util.ArrayList;

public class MainModel {

    private Context context;

    private RobotConnectionModel robotConnectionModel;
    private Handshake handshake;
    private VideoConnectionModel videoConnectionModel;

    private Robot robot;
    private Controller controller;

    // Model for ModelSelectionFragment
    private ModelSelectionFragment modelSelectionFragment;

    // Model for EditControlsFragment
    // TODO Liste von eigener controller klasse???????

    // Model for VideoConnectionFragment
    private URLSettings urlSettings;
    private WebClient wc;
    private SessionData session;
    private LocalDatabase localDatabase;


    public MainModel(@NonNull Application application) {
        /*localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                    }
                })
                .build();

         */

        localDatabase = LocalDatabase.getInstance(application);

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        robot = new EV3(localDatabase.robotModelDAO());

        localDatabase.robotModelDAO().insertAll(new RobotModel(99,"test", "EV3", "joystick:50;1,8|slider:30;4|button:20;2"));

        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
        handshake = new EV3BluetoothHandshake();
        //handshake = new AcceptAllHandshake();
        robotConnectionModel = new BluetoothModel(localDatabase.connectedDeviceDAO(), handshake);
        //TODO: don't hard code robotType
        modelSelectionFragment = new ModelSelectionFragment(localDatabase.robotModelDAO(), "EV3");
        videoConnectionModel = new VideoConnectionModel(localDatabase.localPreferenceDAO());
    }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return robotConnectionModel.getConnectionStatus();
    }

    public void saveURLs(URLSettings.Triple urls) {
        videoConnectionModel.saveURLs(urls);
    }

    public void startConnection(Device device) {
        robotConnectionModel.startConnection(device);
        controller = robot.getController(99, ((BluetoothModel) robotConnectionModel).getService());
    }

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    public void invitePartner() {
        videoConnectionModel.setController(controller);
        videoConnectionModel.invitePartner();
    }

//    public MutableLiveData<String> getInviteLink() {
//        return videoConnectionModel.getInviteLink();
//    }

    public String getShareURL() {
        return videoConnectionModel.getShareURL();
    }

    public JitsiMeetConferenceOptions getOptions() {
        return videoConnectionModel.getOptions();
    }

    public void cancelConnection() {
        robotConnectionModel.cancelConnection();
    }

    public void deviceAccepted() {
        robotConnectionModel.deviceAccepted();
    }

    public void setReceiveCommands() { videoConnectionModel.setReceiveCommands(); }
}
