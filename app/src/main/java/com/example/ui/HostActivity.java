package com.example.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.rcvc.R;
import com.example.ui.robotConnection.BluetoothFragment;
import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeetActivityInterface;

/**
 * Hosts all fragments. See {@link android.app.Activity Activity}.
 */
public class HostActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private Toast toast;

    // Observer to check if bluetooth connection status
    public final Observer<Integer> connectionStatusObserver = newConnectionStatus -> {
        //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost
        HostedFragment currentFragment = (HostedFragment)getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_tag_hosted));
        currentFragment.robotConnectionStatusChanged(newConnectionStatus);
    };

    public HostActivity() {
        super(R.layout.host);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getConnectionStatus().observe(this, connectionStatusObserver);

        //set the title
        setTitle(R.string.title_bluetooth);

        //disables nightmode even if nightmode is activated on the device
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (savedInstanceState == null) { //In the previous example, note that the fragment transaction is only created when savedInstanceState is null. This is to ensure that the fragment is added only once, when the activity is first created. When a configuration change occurs and the activity is recreated, savedInstanceState is no longer null, and the fragment does not need to be added a second time, as the fragment is automatically restored from the savedInstanceState.
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, BluetoothFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .commit();
        }
    }

    public void showToast(String message) {
        if(toast ==null)
            toast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );
        toast.setText(message);
        toast.show();
    }

    public void showToast(int messageId){
        showToast(getResources().getString(messageId));
    }
}
