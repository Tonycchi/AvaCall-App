package com.example.ui.editControls;

import android.service.controls.Control;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;

import java.util.ArrayList;

public class EV3ControlAdapter extends ControlAdapter{

    private final String TAG = "EV3ControlAdapter";

    public EV3ControlAdapter(HostActivity activity, RobotModel model) {
        super(activity, model);
        maxNumberOfMotors = 4;
    }

    protected void initElements(String specs) {
        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();

        for (String t : tmp) {
            list.add(t.split(":"));
        }

        int motors = 0;
        // now translate each $attributes$ into corresponding Objects:
        for (String[] k : list) {
            String[] attrs = k[1].split(";");
            int maxPower = Integer.parseInt(attrs[0]);
            int[] ports;
            switch (k[0]) {
                case "joystick":
                    // $maxPower$;$right$,$left$
                    String[] portsString = attrs[1].split(",");
                    ports = new int[2];
                    ports[0] = portToIndex(Integer.parseInt(portsString[0]));
                    ports[1] = portToIndex(Integer.parseInt(portsString[1]));
                    elements.add(new Integer[]{JOYSTICK, maxPower, ports[0], ports[1]});
                    motors+=2;
                    break;
                case "slider":
                    // $maxPower$;$port$
                    ports = new int[1];
                    ports[0] = portToIndex(Integer.parseInt(attrs[1]));
                    elements.add(new Integer[]{SLIDER, maxPower, ports[0]});
                    motors++;
                    break;
                case "button":
                    // $maxPower$;$port$;$duration$
                    ports = new int[1];
                    ports[0] = portToIndex(Integer.parseInt(attrs[1]));
                    int dur = Integer.parseInt(attrs[2]);
                    elements.add(new Integer[]{BUTTON, maxPower, ports[0], dur});
                    motors++;
                    break;
                default:
                    Log.e(TAG, "this control type is not available");
                    break;
            }
        }
        motorCount = motors;
        itemCount = list.size();
    }

    private int portToIndex(int port) {
        int y = 0, i = port;
        while (i > 1) {
            i = i >> 1;
            y++;
        }
        return y;
    }
    
    // controlElements
    class EV3JoystickHolder extends ControlAdapter.JoystickHolder {

        public EV3JoystickHolder(@NonNull View itemView, int pos) {
            super(itemView);
            Integer t;

            t = elements.get(pos)[1];
            if (t != null) {
                EditText edit = itemView.findViewById(R.id.edit_max_power);
                edit.setText(t.toString());
            }

            t = elements.get(pos)[2];
            if (t != null) {
                RadioGroup radioRight = itemView.findViewById(R.id.radio_port_right);
                ((RadioButton) (radioRight.getChildAt(t))).setChecked(true);
            }

            t = elements.get(pos)[3];
            if (t != null) {
                RadioGroup radioLeft = itemView.findViewById(R.id.radio_port_left);
                ((RadioButton) (radioLeft.getChildAt(t))).setChecked(true);
            }
        }
    }
    @Override
    protected JoystickHolder getJoystickHolder(View itemView, int pos) {
        return new EV3JoystickHolder(itemView, pos);
    }
    @Override
    protected int getJoystickHolderLayout(){
        return R.layout.ev3_joystick;
    }

    class EV3SliderHolder extends ControlAdapter.SliderHolder {

        public EV3SliderHolder(@NonNull View itemView, int pos) {
            super(itemView);
            Integer t;

            t = elements.get(pos)[1];
            if (t != null) {
                EditText edit = itemView.findViewById(R.id.edit_max_power);
                edit.setText(t.toString());
            }

            t = elements.get(pos)[2];
            if (t != null) {
                RadioGroup radio = itemView.findViewById(R.id.radio_port);
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
            }
        }
    }
    @Override
    protected SliderHolder getSliderHolder(View itemView, int pos) {
        return new EV3SliderHolder(itemView, pos);
    }
    @Override
    protected int getSliderHolderLayout(){
        return R.layout.ev3_slider;
    }

    class EV3ButtonHolder extends ControlAdapter.ButtonHolder {

        public EV3ButtonHolder(@NonNull View itemView, int pos) {
            super(itemView);
            Integer t;

            t = elements.get(pos)[1];
            if (t != null) {
                EditText edit = itemView.findViewById(R.id.edit_max_power);
                edit.setText(t.toString());
            }

            t = elements.get(pos)[2];
            if (t != null) {
                RadioGroup radio = itemView.findViewById(R.id.radio_port);
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
            }

            t = elements.get(pos)[3];
            if (t != null) {
                EditText editDur = itemView.findViewById(R.id.edit_duration);
                editDur.setText(t.toString());
            }
        }
    }
    @Override
    protected ButtonHolder getButtonHolder(View itemView, int pos) {
        return new EV3ButtonHolder(itemView, pos);
    }
    @Override
    protected int getButtonHolderLayout(){
        return R.layout.ev3_button;
    }
    
}
