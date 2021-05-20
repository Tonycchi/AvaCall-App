package com.example;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.example.model.AvaCallModel;
import com.example.model.SessionData;

import java.util.List;

public class AvaCallViewModel extends AndroidViewModel {

    AvaCallModel model = new AvaCallModel(PreferenceManager.getDefaultSharedPreferences(getApplication()));

    // Data for BluetoothFragment
    private MutableLiveData<Boolean> bluetoothConnected;
    private MutableLiveData<List<String>> pairedDevicesList;

    // Data for ModelSelectionFragment
    private MutableLiveData<String> selectedModel; //TODO evtl String abändern (je nachdem wie wir Modelle abspeichern wollen)
    private MutableLiveData<List<String>> modelList;

    // Data for EditControlsFragment
    private MutableLiveData<String> selectedModelName;
    private MutableLiveData<List<String>> robotModelList;
    private MutableLiveData<Boolean> controllerSettings; //TODO eigene Klasse für die Controllerauswahl erstellen

    public AvaCallViewModel(@NonNull Application application) {
        super(application);
    }

    public void invitePartner() {
        model.invitePartner();
    }

    public MutableLiveData<String> getInviteLink() {
        return model.getInviteLink();
    }

    public SessionData getSession() {
        return model.getSession();
    }

}
