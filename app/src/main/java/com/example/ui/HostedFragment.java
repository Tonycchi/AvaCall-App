package com.example.ui;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

public abstract class HostedFragment extends Fragment {

    public HostedFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
    }

    public abstract void connectionStatusChanged(Integer newConnectionStatus);
}
