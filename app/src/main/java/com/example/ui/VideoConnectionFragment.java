package com.example.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.AvaCallViewModel;
import com.example.rcvc.R;

import org.jitsi.meet.sdk.JitsiMeetActivity;

public class VideoConnectionFragment extends Fragment {

    private AvaCallViewModel viewModel;

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
        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);
        Observer<String> sharedLinkObserver = link -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.jitsi_room_link), link);
                clipboard.setPrimaryClip(clip);
//                    showToast(getString(R.string.toast_link_copied)); TODO: showToast implementieren
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        };

        viewModel.getInviteLink().observe(getActivity(), sharedLinkObserver);
//
        Button buttonURLsettings = view.findViewById(R.id.button_url_settings);
        Button buttonInvitePartner = view.findViewById(R.id.button_invite_partner);
        Button buttonAccessVideoCall = view.findViewById(R.id.button_access_videocall);
        Button buttonTestControls = view.findViewById(R.id.button_test_controls);

        buttonURLsettings.setOnClickListener(this::openURLSettings);
        buttonInvitePartner.setOnClickListener(this::onClickInvitePartner);
        buttonTestControls.setOnClickListener(this::onClickTestControls);
        buttonAccessVideoCall.setOnClickListener(this::onClickSwitchToVideoCall);

        getActivity().setTitle(R.string.title_video_connection);
    }

    private void openURLSettings(View v) {
        new URLDialogFragment().show(
                getChildFragmentManager(), URLDialogFragment.TAG);
    }

    private void onClickTestControls(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void onClickInvitePartner(View v) {
        viewModel.invitePartner();
    }

    private void onClickSwitchToVideoCall(View v) {
        // TODO setReceiveCommands kommt hier noch hin
        JitsiMeetActivity.launch(getActivity(), viewModel.getSession().getOptions());
    }
}
