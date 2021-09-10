package com.example.ui.editControls;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private RobotModel selectedRobotModel;
    private EditText editName;
    private EditText editDescription;

    public EditControlsFragment() {
        super(R.layout.edit_controls);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        selectedRobotModel = viewModel.getSelectedRobotModel();
        String type = viewModel.getCurrentRobotType();

        switch (type) {
            case Constants.TYPE_EV3:
                controlAdapter = new EV3ControlAdapter((HostActivity) getActivity(), selectedRobotModel);
                break;
            default:
                Log.e(TAG, "ModelType not available for edit model");
                break;
        }

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button buttonEditModelNext = view.findViewById(R.id.button_edit_model_next);
        Button buttonEditModelBack = view.findViewById(R.id.button_edit_model_back);

        editName = view.findViewById(R.id.edit_model_name);
        editDescription = view.findViewById(R.id.edit_model_description);

        if (selectedRobotModel != null) {
            editName.setText(selectedRobotModel.name);
            editDescription.setText(selectedRobotModel.description);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_trash, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_delete) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // delete if robot model exists
                        if (selectedRobotModel != null)
                            viewModel.deleteModelById(selectedRobotModel.id);

                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Dieses Modell löschen?")
                    .setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nein", dialogClickListener)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickButtonEditModelNext(View v) {
        // Log.d(TAG, "" + (robotModel != null) +" "+ (controlAdapter != null) +" "+ (controlAdapter.isReadyToSave()) +" "+ (editName.getText().length() > 0));

        if (controlAdapter != null && controlAdapter.isReadyToSave() && editName.getText().length() > 0) {
            int id;
            if (selectedRobotModel != null) id = selectedRobotModel.id;
            else id = 0;

            viewModel.saveModel(id, editName.getText().toString(), editDescription.getText().toString(), viewModel.getCurrentRobotType(), controlAdapter.getValues());

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
