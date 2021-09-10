package com.example.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.rcvc.R;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

public class VideoConnectionFragment extends HostedFragment {

    private static final String TAG = "VideoConnectionFragment";

    private MainViewModel viewModel;

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
        Button buttonAccessVideoCall = view.findViewById(R.id.button_access_videocall);
        Button buttonTestControls = view.findViewById(R.id.button_test_controls);

        meetingIdTextView = view.findViewById(R.id.text_meeting_id);

        buttonURLsettings.setOnClickListener(this::openURLSettings);
        buttonInvitePartner.setOnClickListener(this::onClickInvitePartner);
        buttonTestControls.setOnClickListener(this::onClickTestControls);
        buttonAccessVideoCall.setOnClickListener(this::onClickSwitchToVideoCall);

        //TODO: delete method
        testStallStuff(view);

        if(viewModel.getID() != null)
            meetingIdTextView.setText(getString(R.string.meeting_id)+" "+viewModel.getID());

        requireActivity().setTitle(R.string.title_video_connection);

        buttonAccessVideoCall.setEnabled(false);
        final Observer<Boolean> videoReadyObserver = buttonAccessVideoCall::setEnabled;

        viewModel.isVideoReady().observe(getViewLifecycleOwner(), videoReadyObserver);
    }

    private void testStallStuff(View view){
        Button button = view.findViewById(R.id.test_send_stall_button);

        RadioGroup element = view.findViewById(R.id.test_radio_element);
        EditText idInput = view.findViewById(R.id.test_editText_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.getText().toString().startsWith("start")){
                    RadioButton rb = view.findViewById(element.getCheckedRadioButtonId());
                    String controlElementId = idInput.getText().toString();
                    String controlElementType = rb.getText().toString().toLowerCase();
                    Log.d(TAG, "start: checked:"+controlElementType+" id:"+controlElementId);
                    button.setText("end stall");
                    if(controlElementId!="")
                        viewModel.sendStallDetected(controlElementType, Integer.parseInt(controlElementId));
                }else{
                    RadioButton rb = view.findViewById(element.getCheckedRadioButtonId());
                    String controlElementId = idInput.getText().toString();
                    String controlElementType = rb.getText().toString().toLowerCase();
                    Log.d(TAG, "end: checked:"+controlElementType+" id:"+controlElementId);
                    button.setText("start stall");
                    if(controlElementId!="")
                        viewModel.sendStallEnded(controlElementType, Integer.parseInt(controlElementId));
                }
            }
        });
    }


    private void openURLSettings(View v) {
        new URLDialogFragment().show(
                getChildFragmentManager(), URLDialogFragment.TAG);
        viewModel.cancelServerConnection();
        meetingIdTextView.setText("");
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
            // TODO setReceiveCommands kommt hier noch hin
            JitsiMeetActivity.launch(requireContext(), (JitsiMeetConferenceOptions) viewModel.getOptions());
            viewModel.setReceiveCommands();
        }else{
            ((HostActivity)getActivity()).showToast(R.string.connect_to_server);
        }
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity) getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}
