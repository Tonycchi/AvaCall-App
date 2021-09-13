package com.example.ui.serverConnection;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.MainViewModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

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

        //Observe if video call is terminated
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object error = intent.getExtras().get("error");
                Object url = intent.getExtras().get("url");
                Log.d(TAG, "User hung up "+url+" with error:"+error);
                closeVideoCall();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
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
        closeVideoCall();
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
            JitsiMeetActivity.launch(requireContext(), (JitsiMeetConferenceOptions) viewModel.getOptions());
            viewModel.setReceiveCommands(true);
        }else{
            ((HostActivity)getActivity()).showToast(R.string.connect_to_server);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy Jitsi");
        closeVideoCall();
    }

    private void closeVideoCall(){
        Intent muteBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        if(getActivity()!=null)
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(muteBroadcastIntent);
        viewModel.setReceiveCommands(false);
    }

    @Override
    public void robotConnectionStatusChanged(Integer newConnectionStatus) {
        ((HostActivity) getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}
