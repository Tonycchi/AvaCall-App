package com.example.rcvc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BluetoothFragment extends ConnectionFragment {


    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button buttonFirstConnection = (Button) view.findViewById(R.id.button_first_connection);
        buttonFirstConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFirstBluetoothConnection();
            }
        });
    }

    private void onClickFirstBluetoothConnection(){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ModelSelectionFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

    }


}
