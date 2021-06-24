package com.example.ui;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.data.RobotModel;
import com.example.data.RobotModelDAO;
import com.example.rcvc.R;
import com.example.ui.editControls.EditControlsFragment;

import java.util.List;
import java.util.stream.Collectors;

public class ModelSelectionFragment extends HostedFragment {

    private static final String TAG = "ModelSelectionFragment";
    private NumberPicker modelPicker;
    private RobotModelDAO robotModelDAO;
    private String robotType;

    public ModelSelectionFragment(RobotModelDAO robotModelDAO, String robotType) {
        super(R.layout.model_selection);
        this.robotModelDAO = robotModelDAO;
        this.robotType = robotType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        Button useModel = view.findViewById(R.id.button_use_model);
        Button editModel = view.findViewById(R.id.button_edit_model);

        useModel.setOnClickListener(this::onClickUseModel);

        editModel.setOnClickListener(this::onClickEditModel);

        modelPicker = view.findViewById(R.id.model_picker);
        List<RobotModel> allRobots = robotModelDAO.getAllModelsOfType(robotType);
        int numberOfRobots = allRobots.size();
        String[] allRobotNames = new String[numberOfRobots];
        for(int i=0; i<numberOfRobots; i++){
            allRobotNames[i] = allRobots.get(i).getName();
        }
        modelPicker.setMaxValue(numberOfRobots-1);
        modelPicker.setMinValue(0);
        modelPicker.setWrapSelectorWheel(true);
        modelPicker.setDisplayedValues(allRobotNames);

        getActivity().setTitle(R.string.title_model_selection);
    }

    private void onClickEditModel(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickUseModel(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt("cameFromModelSelection", 1);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, TestRobotFragment.class, bundle, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus) {
         //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost, 4 connection is accepted = correct device, 5 connection is not accepted = wrong device
        switch (newConnectionStatus) {
            case 3:
                Log.d(TAG, "Case 3: Connection lost!");
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.connection_lost));
                break;

            default:
                Log.d(TAG, "Default: Something strange or nothing(Case -1) happend with the connection.");
                break;
        }

    }
}
