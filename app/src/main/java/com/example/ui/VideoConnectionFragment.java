package com.example.ui;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.rcvc.R;

public class VideoConnectionFragment extends Fragment {

    public VideoConnectionFragment() {
        super(R.layout.video_connection);
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
        Button buttonURLsettings = view.findViewById(R.id.button_url_settings);
        Button buttonInvitePartner = view.findViewById(R.id.button_invite_partner);
        Button buttonAccessVideoCall = view.findViewById(R.id.button_access_videocall);
        Button buttonTestControls = view.findViewById(R.id.button_test_controls);

        buttonTestControls.setOnClickListener(this::onClickTestControls);

        getActivity().setTitle(R.string.title_video_connection);
    }

    private void onClickTestControls(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }
}
