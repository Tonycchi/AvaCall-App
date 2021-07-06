package com.example.ui.editControls.ev3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.rcvc.R;

public class EditButton extends EditControlElement {

    public EditButton() {
        super(R.layout.ev3_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceState) {
        return inflater.inflate(R.layout.ev3_button, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }
}
