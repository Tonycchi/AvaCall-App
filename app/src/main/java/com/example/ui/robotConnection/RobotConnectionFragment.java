package com.example.ui.robotConnection;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.example.model.robotConnection.Device;

public abstract class RobotConnectionFragment extends Fragment {

    public RobotConnectionFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
    }

    public abstract void onClickDevice(Device device);
}
