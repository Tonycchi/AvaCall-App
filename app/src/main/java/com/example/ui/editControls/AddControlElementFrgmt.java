package com.example.ui.editControls;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rcvc.R;

public class AddControlElementFrgmt extends DialogFragment {

    public static final String TAG = "AddControlElementFrgmt";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context c = getContext();
        ListView l = new ListView(c);
        l.setAdapter(ArrayAdapter.createFromResource(c, R.array.control_elements_names, android.R.layout.simple_list_item_1));
        l.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT));
        // TODO make it that the items fill the entire width
        FrameLayout f = new FrameLayout(c);
        f.addView(l);
        return f;
    }
}
