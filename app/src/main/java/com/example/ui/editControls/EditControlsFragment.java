package com.example.ui.editControls;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Constants;
import com.example.MainViewModel;
import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;
import com.example.ui.TestRobotFragment;

public class EditControlsFragment extends HostedFragment {

    private final String TAG = "EditControlsFragment";

    private ControlAdapter controlAdapter;
    private RecyclerView editControlsList;
    private MainViewModel viewModel;
    private RobotModel robotModel;
    private EditText editName;
    private EditText editDescription;

    public EditControlsFragment() {
        super(R.layout.edit_controls);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "A");
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        robotModel = viewModel.getSelectedRobotModel();

        switch (robotModel.type) {
            case Constants.TYPE_EV3:
                controlAdapter = new EV3ControlAdapter((HostActivity) getActivity(), robotModel);
                break;
            default:
                Log.e(TAG, "ModelType not available for edit model");
                break;
        }

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button buttonEditModelNext = view.findViewById(R.id.button_edit_model_next);
        Button buttonEditModelBack = view.findViewById(R.id.button_edit_model_back);

        if (robotModel != null) {
            editName = view.findViewById(R.id.edit_model_name);
            editName.setText(robotModel.name);
            editDescription = view.findViewById(R.id.edit_model_description);
            editDescription.setText(robotModel.description);
        } else {
            Log.e(TAG, "robotModel == null");
        }

        if (controlAdapter != null) {
            editControlsList = view.findViewById(R.id.list_edit);
            editControlsList.setLayoutManager(new LinearLayoutManager(getContext()));
            editControlsList.setAdapter(controlAdapter);
        } else {
            Log.e(TAG, "controlAdapter == null");
        }

        buttonEditModelNext.setOnClickListener(this::onClickButtonEditModelNext);
        buttonEditModelBack.setOnClickListener(this::onClickButtonEditModelBack);

        getActivity().setTitle(R.string.title_edit_controls);
    }

    private void onClickButtonEditModelNext(View v) {
        // Log.d(TAG, "" + (robotModel != null) +" "+ (controlAdapter != null) +" "+ (controlAdapter.isReadyToSave()) +" "+ (editName.getText().length() > 0));

        if (robotModel != null && controlAdapter != null && controlAdapter.isReadyToSave() && editName.getText().length() > 0) {
            viewModel.saveModel(robotModel.id, editName.getText().toString(), editDescription.getText().toString(), robotModel.type, controlAdapter.getValues());

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, TestRobotFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickButtonEditModelBack(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity) getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}
