package com.example.ui.editControls;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rcvc.R;

public class AddControlElementFragment extends DialogFragment {

    public static final String TAG = "AddControlElementFragment";
    private final ControlAdapter adapter;

    public AddControlElementFragment(ControlAdapter adapter) {
        this.adapter = adapter;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context c = getContext();
        ListView l = new ListView(c);
        //TODO: make for land (big_element_width_land)
        l.setLayoutParams(new ListView.LayoutParams((int)getResources().getDimension(R.dimen.big_element_width), ListView.LayoutParams.MATCH_PARENT));
        l.setAdapter(ArrayAdapter.createFromResource(c, R.array.ev3_control_elements_names, R.layout.simple_list_item));

        l.setOnItemClickListener((AdapterView<?> arg0, View arg1, int position, long arg3) -> {
            adapter.addElement(position + 1);
            this.dismiss();
        });

        FrameLayout f = new FrameLayout(c);
        f.addView(l);
        return f;
    }
}
