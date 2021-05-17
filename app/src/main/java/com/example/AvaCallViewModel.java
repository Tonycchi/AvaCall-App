package com.example;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.model.AvaCallModel;

import java.util.ArrayList;
import java.util.List;

public class AvaCallViewModel extends ViewModel {

    private AvaCallModel model;

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

    public AvaCallViewModel(){
        model = new AvaCallModel();
    }

    public void updatePairedDevices() {
        model.updatePairedDevices();
    }

    public MutableLiveData<ArrayList<String>> getPairedDevicesName() {
        return model.getPairedDevicesName();
    }
}
