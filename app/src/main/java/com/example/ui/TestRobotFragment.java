package com.example.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.model.robot.Controller;
import com.example.model.robot.ev3.EV3Controller;
import com.example.rcvc.R;
import com.example.ui.editControls.EditControlsFragment;

import java.util.ArrayList;
import java.util.List;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TestRobotFragment extends HostedFragment {

    private static final String TAG = "TestRobotFragment";

    //true if this fragment was started directly from model selection
    private boolean cameFromModelSelection;

    private MainViewModel viewModel;

    public TestRobotFragment(){super(R.layout.test_robot);}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameFromModelSelection = false;
        try {
            cameFromModelSelection = (requireArguments().getInt("cameFromModelSelection") == 1);
        } catch(IllegalStateException ignore) { }

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.test_robot_fragment);
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

//        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.button_neutral);
//
//        Button button0 = new Button(newContext);
//        Button button1 = new Button(newContext);
//
//        button0.setText("KEKW");
//        button0.setId(View.generateViewId());
//        button0.setBackgroundResource(R.drawable.standard_button);
//        constraintLayout.addView(button0);
//
//        button1.setText("Gaynse");
//        button1.setId(View.generateViewId());
//        button1.setBackgroundResource(R.drawable.standard_button);
//        constraintLayout.addView(button1);

//        set.connect(button0.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) getResources().getDimension(R.dimen.margin_top));
//        set.connect(button0.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int) getResources().getDimension(R.dimen.margin_side));
//        set.connect(button0.getId(), ConstraintSet.LEFT, button1.getId(), ConstraintSet.RIGHT, (int) getResources().getDimension(R.dimen.margin_horizontal_small));
//        set.connect(button0.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.margin_bottom));
//        set.constrainHeight(button0.getId(), (int) getResources().getDimension(R.dimen.standard_button_height));
//
//        set.connect(button1.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) getResources().getDimension(R.dimen.margin_top));
//        set.connect(button1.getId(), ConstraintSet.RIGHT, button0.getId(), ConstraintSet.LEFT, (int) getResources().getDimension(R.dimen.margin_horizontal_small));
//        set.connect(button1.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) getResources().getDimension(R.dimen.margin_side));
//        set.connect(button1.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.margin_bottom));
//        set.constrainHeight(button1.getId(), (int) getResources().getDimension(R.dimen.standard_button_height));
//        set.applyTo(constraintLayout);

        //joystick|button|slider|button
       String t = viewModel.getSelectedModelElements();
       String[] order = rankOrder(t);
      // Object[] controlElements = createControlElements(order, constraintLayout);
        createControlElements(order, constraintLayout);
       //String[] controlElements = t.split("\\|");
//
        JoystickView joystick;
        SeekBar slider;
        Button buttonFire;
//
//        for (int i=0; i< controlElements.length; i++) {
//            Log.d(TAG, "controlElement: " + controlElements[i]);
//            int id = i;
//            switch (controlElements[i]) {
//                case "joystick":
//                    joystick = view.findViewById(R.id.joystick);
//                    joystick.setVisibility(View.VISIBLE);
//
//                    joystick.setOnMoveListener((angle, strength) -> {
//                        viewModel.sendControlInput(id, angle, strength);
////                        Log.d(TAG, "Joystick angle;strength: " + angle + ";" + strength);
//                    });
//                    break;
//                case "slider":
//                    slider = view.findViewById(R.id.slider);
//                    slider.setVisibility(View.VISIBLE);
//
//                    slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                            viewModel.sendControlInput(id, progress);
////                            Log.d(TAG, "Slider deflection: " + String.valueOf(progress));
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//                            viewModel.sendControlInput(id, 50);
//                            seekBar.setProgress(50);
//                        }
//                    });
//                    break;
//                case "button":
//                    Log.d(TAG, "dreckiger Button");
//                    buttonFire = view.findViewById(R.id.button_fire);
//                    buttonFire.setVisibility(View.VISIBLE);
//
//                    buttonFire.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            switch(event.getAction()) {
//                                case MotionEvent.ACTION_DOWN:
//                                    viewModel.sendControlInput(id, 1);
////                                    Log.d(TAG, "Button activity: " + 1);
//                                    break;
//                                case MotionEvent.ACTION_UP:
////                                    Log.d(TAG, "Button activity: " + 0);
//                                    break;
//                            }
//                            return true;
//                        }
//                    });
//                    break;
//            }
//        }

        Button buttonYes = view.findViewById(R.id.button_yes);
        Button buttonNo = view.findViewById(R.id.button_no);

        buttonYes.setOnClickListener(this::onClickYes);
        buttonNo.setOnClickListener(this::onClickNo);

        getActivity().setTitle(R.string.title_test_robot);
    }

    private void onClickYes(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, VideoConnectionFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }
    private void onClickNo(View v){
        FragmentManager fragmentManager = getParentFragmentManager();
        if(cameFromModelSelection) {    //if cameFromModelSelection: pop to modelselection and then switch to editcontrols
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }else{  //if cameFromEdit: simply pop back
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus) {
        //TODO: implement
        ((HostActivity)getActivity()).showToast("Irgendwas mit Bluetooth hat sich geändert - noch nicht weiter geregelt, was jetzt passiert!");
    }

    public String[] rankOrder(String input) {
        String[] cases = {"joystick", "slider", "button"};
        List<String> temp = new ArrayList<String>();
        int lastIndex = 0;
        int count = 0;
        int i = 0;
        for (String s : cases) {
            lastIndex = 0;
            count = 0;
            while (lastIndex != -1) {
                lastIndex = input.indexOf(s, lastIndex);
                if (lastIndex != -1) {
                    temp.add(s);
                    lastIndex += s.length();
                }
            }
            //result[i] = count;
            i++;
        }
        Log.d(TAG, "Reihenfolge" + temp);
        String result[] = new String[temp.size()];
        temp.toArray(result);
        return result;
    }
    //TODO ConstraintSets Variabel, Buttons implementieren, Slider rotation fixen, Joystick Color fixen,
    public void createControlElements(String[] order, ConstraintLayout constraintLayout) {
        Object[] controlElements = new Object[4];
        for (int i = 0; i < order.length; i++) {
            ConstraintSet set = new ConstraintSet();
            // Log.d(TAG, "controlElement: " + controlElements[i]);
            int id = i;
            switch (order[i]) {
                case "joystick":
                    JoystickView joystick = new JoystickView(getContext());
                    joystick.setId(View.generateViewId());
                    joystick.setBorderColor(getResources().getColor(R.color.joystick_border));
                    joystick.setButtonColor(getResources().getColor(R.color.joystick_button));
                    joystick.setBackgroundColor(getResources().getColor(R.color.joystick_background));
                    joystick.setButtonSizeRatio(30);
                    joystick.setBackgroundSizeRatio(50);
                    constraintLayout.addView(joystick);
                  //  ConstraintSet set = new ConstraintSet();
                    set.connect(joystick.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) getResources().getDimension(R.dimen.margin_top));
                    set.connect(joystick.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int) getResources().getDimension(R.dimen.margin_side));
                    set.connect(joystick.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.margin_bottom));
                    set.constrainHeight(joystick.getId(), (int) getResources().getDimension(R.dimen.joystick_size));
                    set.applyTo(constraintLayout);

                    joystick.setOnMoveListener((angle, strength) -> {
                        viewModel.sendControlInput(id, angle, strength);
                        Log.d(TAG, "Joystick angle;strength: " + angle + ";" + strength);
                    });

                    controlElements[i] = joystick;
                    break;
                case "slider":
                    SeekBar slider = new SeekBar(getContext());
                    slider.setId(View.generateViewId());
                    slider.setProgress(50);
                    slider.setThumb(getContext().getDrawable(R.drawable.slider_thumb));
                    slider.setProgressDrawable(getContext().getDrawable(R.drawable.slider_progressbar));
                    slider.setRotation(-90);
                    constraintLayout.addView(slider);

                    set.connect(slider.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) getResources().getDimension(R.dimen.margin_top));
                    set.connect(slider.getId(), ConstraintSet.RIGHT, ((JoystickView) controlElements[i-1]).getId(), ConstraintSet.LEFT, (int) getResources().getDimension(R.dimen.margin_horizontal_small));
                    set.connect(slider.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) getResources().getDimension(R.dimen.margin_side));
                    set.connect(slider.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.margin_bottom));
                    set.constrainHeight(slider.getId(), (int) getResources().getDimension(R.dimen.joystick_size));
                    set.applyTo(constraintLayout);

                    slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            viewModel.sendControlInput(id, progress);
//                            Log.d(TAG, "Slider deflection: " + String.valueOf(progress));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            viewModel.sendControlInput(id, 50);
                            seekBar.setProgress(50);
                        }
                    });
                    controlElements[i] = slider;
                    break;
//                case "button":
//                    Log.d(TAG, "dreckiger Button");
//                    buttonFire = view.findViewById(R.id.button_fire);
//                    buttonFire.setVisibility(View.VISIBLE);
//
//                    buttonFire.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            switch(event.getAction()) {
//                                case MotionEvent.ACTION_DOWN:
//                                    viewModel.sendControlInput(id, 1);
//                                    Log.d(TAG, "Button activity: " + 1);
//                                    break;
//                                case MotionEvent.ACTION_UP:
//                                    Log.d(TAG, "Button activity: " + 0);
//                                    break;
//                            }
//                            return true;
//                        }
//                    });
//                    break;
//            }
//        }
            }
        }
    }
}