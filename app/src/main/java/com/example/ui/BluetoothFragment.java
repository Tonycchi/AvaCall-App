package com.example.ui;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AvaCallViewModel;
import com.example.bluetooth.PairedDevicesCustomAdapter;
import com.example.rcvc.R;

import java.util.ArrayList;

public class BluetoothFragment extends ConnectionFragment {

    private AvaCallViewModel viewModel;

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);

        RecyclerView recycler = view.findViewById(R.id.list_paired_devices);
        recycler.setAdapter(new PairedDevicesCustomAdapter());

        Button buttonFirstConnection = (Button) view.findViewById(R.id.button_first_connection);
        buttonFirstConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFirstBluetoothConnection();
            }
        });

        getActivity().setTitle(R.string.title_bluetooth);
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
