package com.example.rcvc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class VideoConnectionFragment extends Fragment {

    public VideoConnectionFragment() {
        super(R.layout.video_connection);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button buttonURLsettings = (Button) view.findViewById(R.id.button_url_settings);
        Button buttonInvitePartner = (Button) view.findViewById(R.id.button_invite_partner);
        Button buttonAccessVideoCall = (Button) view.findViewById(R.id.button_access_videocall);
        Button buttonTestControls = (Button) view.findViewById(R.id.button_test_controls);

        buttonTestControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTestControlls();
            }
        });
    }

    private void onClickTestControlls(){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
