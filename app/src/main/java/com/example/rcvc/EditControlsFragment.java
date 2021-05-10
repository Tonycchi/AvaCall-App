package com.example.rcvc;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class EditControlsFragment extends Fragment {

    public EditControlsFragment() {
        super(R.layout.edit_controls);
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
        Button buttonEditModelNext = view.findViewById(R.id.button_edit_model_next);
        Button buttonEditModelBack = view.findViewById(R.id.button_edit_model_back);

        buttonEditModelNext.setOnClickListener(this::onClickButtonEditModelNext);
        buttonEditModelBack.setOnClickListener(this::onClickButtonEditModelBack);

        getActivity().setTitle(R.string.title_edit_controls);
    }

    private void onClickButtonEditModelNext(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, TestRobotFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickButtonEditModelBack(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }


}
