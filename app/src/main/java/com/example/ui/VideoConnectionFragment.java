package com.example.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.rcvc.R;

public class VideoConnectionFragment extends HostedFragment {

    private static final String TAG = "VideoConnectionFragment";

    private MainViewModel viewModel;

    private Button buttonCancelConnection;
    private Button buttonAccessVideoCall;

    private TextView meetingIdTextView;

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
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        Button buttonURLsettings = view.findViewById(R.id.button_url_settings);
        Button buttonInvitePartner = view.findViewById(R.id.button_invite_partner);
        buttonCancelConnection = view.findViewById(R.id.button_cancel_connection);
        buttonAccessVideoCall = view.findViewById(R.id.button_access_videocall);
        Button buttonTestControls = view.findViewById(R.id.button_test_controls);

        meetingIdTextView = view.findViewById(R.id.text_meeting_id);

        buttonURLsettings.setOnClickListener(this::openURLSettings);
        buttonInvitePartner.setOnClickListener(this::onClickInvitePartner);
        buttonTestControls.setOnClickListener(this::onClickTestControls);
        buttonAccessVideoCall.setOnClickListener(this::onClickSwitchToVideoCall);
        buttonCancelConnection.setOnClickListener(this::onClickCancelConnection);

        if(viewModel.getID() != null && viewModel.isVideoReady().getValue())
            meetingIdTextView.setText(getString(R.string.meeting_id)+" "+viewModel.getID());

        requireActivity().setTitle(R.string.title_video_connection);

        videoReady(false);
        final Observer<Boolean> videoReadyObserver = this::videoReady;

        viewModel.isVideoReady().observe(getViewLifecycleOwner(), videoReadyObserver);
    }

    private void videoReady(boolean ready){
        buttonAccessVideoCall.setEnabled(ready);
        buttonCancelConnection.setEnabled(ready);
    }

    private void onClickCancelConnection(View view) {
        cancelServerConnection();
    }

    private void cancelServerConnection(){
        viewModel.cancelServerConnection();
        meetingIdTextView.setText("");
    }

    private void openURLSettings(View v) {
        new URLDialogFragment().show(
                getChildFragmentManager(), URLDialogFragment.TAG);
        cancelServerConnection();
    }

    private void onClickTestControls(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void onClickInvitePartner(View v) {
        Log.d(TAG, "onClickInvitePartner");
        if(viewModel.invitePartner()) { //successful connection to server
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Log.d(TAG, "OLD Android Version!! -> no share Link pop-up shown");
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.room_link), viewModel.getShareURL());
                clipboard.setPrimaryClip(clip);
                ((HostActivity) getActivity()).showToast(getString(R.string.toast_link_copied));
            } else {
                Log.d(TAG, "New Android Version!");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, viewModel.getShareURL());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }

            meetingIdTextView.setText(getString(R.string.meeting_id) + " " + viewModel.getID());

        }else{ //failed to connect to server
            ((HostActivity)getActivity()).showToast(R.string.server_connection_failed);
        }

    }

    private void onClickSwitchToVideoCall(View v) {
        if(viewModel.isConnectedToServer()) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, JitsiFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }else{
            ((HostActivity)getActivity()).showToast(R.string.connect_to_server);
        }
    }

    @Override
    public void robotConnectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity) getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}
