package com.example.ui.modelSelection;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;
import com.example.ui.TestRobotFragment;
import com.example.ui.editControls.EditControlsFragment;

import net.simonvt.numberpicker.NumberPicker;

public class ModelSelectionFragment extends HostedFragment {

    private static final String TAG = "ModelSelectionFragment";
    private NumberPicker modelPicker;
    private MainViewModel viewModel;
    private TextView modelDescription;
    private ImageView modelPicture;
    private boolean modelsExist;
    private Button useModel;

    public ModelSelectionFragment() {
        super(R.layout.model_selection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        useModel = view.findViewById(R.id.button_use_model);
        Button editModel = view.findViewById(R.id.button_edit_model);


        useModel.setOnClickListener(this::onClickUseModel);
        editModel.setOnClickListener(this::onClickEditModel);

        modelPicker = view.findViewById(R.id.model_picker);

        modelsExist = false;
        refreshNumberPicker();

        modelPicker.setOnValueChangedListener(this::onSelectedModelChanged);

        modelDescription = view.findViewById(R.id.model_description_text);
        modelPicture = view.findViewById(R.id.model_picture);
        setModelDescription();

        getActivity().setTitle(R.string.title_model_selection);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_model, menu);
    }

    private void refreshNumberPicker() {
        String[] allRobotNames = viewModel.getAllRobotNames();
        int maxVal = allRobotNames.length - 1;
        if (maxVal < 0) {
            maxVal = 0;
            modelsExist = false;
        } else {
            modelsExist = true;
        }
        modelPicker.setMaxValue(maxVal);
        modelPicker.setMinValue(0);
        if (modelsExist)
            modelPicker.setDisplayedValues(allRobotNames);
        else
            modelPicker.setDisplayedValues(new String[]{"Keine Modelle vorhanden."});
        modelPicker.setValue(viewModel.getSelectedModelPosition());
        
        useModel.setEnabled(modelsExist);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            viewModel.modelSelected(-1);

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.menu_delete) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // delete model and remove from view
                        int v = modelPicker.getValue();
                        viewModel.deleteSelectedModel(v);

                        refreshNumberPicker();
                        setModelDescription();
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Dieses Modell löschen?:\n\"" + viewModel.getRobotModel(modelPicker.getValue()).name + "\"")
                    .setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nein", dialogClickListener)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Position: " + viewModel.getSelectedModelPosition());
        modelPicker.setValue(viewModel.getSelectedModelPosition());
    }

    private void onSelectedModelChanged(NumberPicker modelPicker, int oldVal, int newVal) {
        viewModel.setSelectedModelPosition(modelPicker.getValue());
        setModelDescription();
    }

    private void setModelDescription() {
        RobotModel robotModel;
        String descriptionText = "";
        if (modelsExist) {
            robotModel = viewModel.getRobotModel(modelPicker.getValue());
        } else {
            robotModel = new RobotModel(-1, "", "", "", "");
        }
        String typeString = (modelsExist) ? "(" + robotModel.type + "):" : "";
        descriptionText = robotModel.description;
        if (descriptionText == null || descriptionText.isEmpty())
            descriptionText = robotModel.specs;
        modelDescription.setText(robotModel.name + typeString + descriptionText);

        //TODO: other picture
        modelPicture.setImageResource(R.drawable.no_image_available);
        Log.d(TAG, "Model at Position " + modelPicker.getValue() + " selected! Name:" + robotModel.name + " Id:" + robotModel.id);
    }

    private void onClickEditModel(View v) {
        if (modelsExist)
            viewModel.modelSelected(modelPicker.getValue());
        else
            viewModel.modelSelected(-1);

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickUseModel(View v) {
        if (modelsExist) {
            viewModel.modelSelected(modelPicker.getValue());

            FragmentManager fragmentManager = getParentFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putInt("cameFromModelSelection", 1);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, TestRobotFragment.class, bundle, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }
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
