package com.example.ui;

import android.content.res.Configuration;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

public abstract class HostedFragment extends Fragment {

    public HostedFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
    }
    public HostedFragment() {
        super();

    }

    public abstract void robotConnectionStatusChanged(Integer newConnectionStatus);

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getParentFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}
