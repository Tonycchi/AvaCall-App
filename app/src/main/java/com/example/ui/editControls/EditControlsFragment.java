package com.example.ui.editControls;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;
import com.example.ui.TestRobotFragment;
import com.example.ui.editControls.ev3.Adapter;
import com.example.ui.editControls.ev3.EditJoystick;

import java.util.ArrayList;

public class EditControlsFragment extends HostedFragment {

    private Adapter adapter;
    private RecyclerView optionsList;

    public EditControlsFragment() {
        super(R.layout.edit_controls);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new Adapter(getActivity());

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button buttonEditModelNext = view.findViewById(R.id.button_edit_model_next);
        Button buttonEditModelBack = view.findViewById(R.id.button_edit_model_back);

        Spinner spinner = view.findViewById(R.id.edit_model_spinner);

        optionsList = view.findViewById(R.id.list_edit);
        optionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsList.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.rotob_model_types,
                        android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        buttonEditModelNext.setOnClickListener(this::onClickButtonEditModelNext);
        buttonEditModelBack.setOnClickListener(this::onClickButtonEditModelBack);

        getActivity().setTitle(R.string.title_edit_controls);
    }

    private void onClickButtonEditModelNext(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, TestRobotFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickButtonEditModelBack(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity)getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}
