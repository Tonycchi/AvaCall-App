package com.example.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MainViewModel;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.util.HashMap;
import java.util.Map;

/**
 * Base {@link Fragment} for applications integrating Jitsi Meet at a higher level. It
 * contains all the required wiring between the {@code JitsiMeetView} and
 * the Fragment lifecycle methods already implemented.
 *
 * In this fragment we use a single {@code JitsiMeetView} instance. This
 * instance gives us access to a view which displays the welcome page and the
 * conference itself. All lifecycle methods associated with this Fragment are
 * hooked to the React Native subsystem via proxy calls through the
 * {@code JitsiMeetActivityDelegate} static methods.
 */
public class JitsiFragment extends HostedFragment {

    private static final String TAG = "JitsiFragment";

    /**
     * Instance of the {@link JitsiMeetView} which this activity will display.
     */
    private JitsiMeetView jitsiView;

    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return this.jitsiView = new JitsiMeetView(getActivity());

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.setReceiveCommands(true);
        jitsiView.join((JitsiMeetConferenceOptions)viewModel.getOptions());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object error = intent.getExtras().get("error");
                Object url = intent.getExtras().get("url");
                Log.d(TAG, "Hung up "+url+" with error:"+error);
                viewModel.setReceiveCommands(false);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy Jitsi");
        JitsiMeetActivityDelegate.onHostDestroy(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setReceiveCommands(true);
        JitsiMeetActivityDelegate.onHostResume(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.setReceiveCommands(false);
        JitsiMeetActivityDelegate.onHostPause(getActivity());
    }

    @Override
    public void robotConnectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity) getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }
}