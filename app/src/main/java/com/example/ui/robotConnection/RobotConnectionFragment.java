package com.example.ui.robotConnection;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.example.model.robotConnection.Device;
import com.example.ui.HostedFragment;

public abstract class RobotConnectionFragment extends HostedFragment {

    public RobotConnectionFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
    }

    public abstract void onClickDevice(Device device);
}
