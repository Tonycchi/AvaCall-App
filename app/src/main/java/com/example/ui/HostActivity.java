package com.example.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.AvaCallViewModel;
import com.example.rcvc.R;

public class HostActivity extends AppCompatActivity {

    private AvaCallViewModel viewModel;

    public HostActivity() {
        super(R.layout.host);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AvaCallViewModel.class);

        //set the title
        setTitle(R.string.title_bluetooth);

        //disables nightmode even if nightmode is activated on the device
        //TODO: implement nightmode and delete this line
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (savedInstanceState == null) { //In the previous example, note that the fragment transaction is only created when savedInstanceState is null. This is to ensure that the fragment is added only once, when the activity is first created. When a configuration change occurs and the activity is recreated, savedInstanceState is no longer null, and the fragment does not need to be added a second time, as the fragment is automatically restored from the savedInstanceState.
            Bundle bundle = new Bundle(); //not needed yet, maybe later
            bundle.putInt("some_int", 0); //TODO: delete if not used

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, BluetoothFragment.class, bundle)
                    .commit();
        }
    }

}
