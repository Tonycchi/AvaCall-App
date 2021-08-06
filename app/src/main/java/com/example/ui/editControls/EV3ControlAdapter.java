package com.example.ui.editControls;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;

import java.util.ArrayList;
import java.util.List;

public class EV3ControlAdapter extends ControlAdapter {

    private final String TAG = "EV3ControlAdapter";

    public EV3ControlAdapter(HostActivity activity, RobotModel model) {
        super(activity, model);
        maxNumberElements = 4;

        // model is not null if called by "Modell Bearbeiten"
        // and null if called by "Neues Modell"
        if (model != null) {
            initElements(model.specs);
        } else {
            Log.e(TAG, "model is null");
            numberOfFields = 0;
            fieldsFilled = 0;
        }
        Log.d(TAG, "constructor: " + fieldsFilled);
        // true if all fields filled out (model != null ==> called by "Modell Bearbeiten")
    }

    @Override
    boolean isReadyToSave() {
        Log.d(TAG, fieldsFilled + " " + numberOfFields);
        return fieldsFilled == numberOfFields && numberOfFields > 0;
    }

    @Override
    List<List<Integer>> getValues() {
        return elementValues;
    }

    // "joystick:50;1,8|slider:30;4|button:20;2;5000"

    private void initElements(String specs) {
        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();
        for (String t : tmp) {
            list.add(t.split(":"));
        }

        int motors = 0, fields = 0;
        // now translate each $attributes$ into corresponding array:
        for (String[] k : list) {
            String[] attrs = k[1].split(";");
            int maxPower = Integer.parseInt(attrs[0]);
            int[] ports;
            switch (k[0]) {
                case "joystick":
                    // get attributes out of string:
                    // $maxPower$;$rightPort$,$leftPort$
                    String[] portsString = attrs[1].split(",");
                    ports = new int[2];
                    ports[0] = portToIndex(Integer.parseInt(portsString[0]));
                    ports[1] = portToIndex(Integer.parseInt(portsString[1]));
                    // and put into list:
                    elementValues.add(newList(JOYSTICK, maxPower, ports[0], ports[1]));
                    motors += 2; // joystick uses 2 motors
                    fields += 3;
                    break;
                case "slider":
                    // get attributes out of string:
                    // $maxPower$;$port$
                    ports = new int[1];
                    ports[0] = portToIndex(Integer.parseInt(attrs[1]));
                    // and put into list:
                    elementValues.add(newList(SLIDER, maxPower, ports[0]));
                    motors++;
                    fields += 2;
                    break;
                case "button":
                    // get attributes out of string:
                    // $maxPower$;$port$;$duration$
                    ports = new int[1];
                    ports[0] = portToIndex(Integer.parseInt(attrs[1]));
                    int dur = Integer.parseInt(attrs[2]);
                    // and put into list:
                    elementValues.add(newList(BUTTON, maxPower, ports[0], dur));
                    motors++;
                    fields += 3;
                    break;
                default:
                    Log.e(TAG, "this control type is not available");
                    break;
            }
        }
        // add button isn't shown when all motors set:
        itemCount = (elementValues.size() == 4) ? list.size() : list.size() + 1;
        fieldsFilled = fields;
        numberOfFields = fields;
    }

    /**
     * @param port ev3 motor port number (powers of 2)
     * @return corresponding index in radio group ( log_2(port) - 1 )
     */
    private int portToIndex(int port) {
        int y = 0, i = port;
        while (i > 1) {
            i = i >> 1;
            y++;
        }
        return y;
    }

    @Override
    protected JoystickHolder getJoystickHolder(View itemView, int pos) {
        return new EV3JoystickHolder(itemView, pos);
    }

    @Override
    protected int getJoystickHolderLayout() {
        return R.layout.ev3_edit_joystick;
    }

    @Override
    protected SliderHolder getSliderHolder(View itemView, int pos) {
        return new EV3SliderHolder(itemView, pos);
    }

    @Override
    protected int getSliderHolderLayout() {
        return R.layout.ev3_edit_slider;
    }

    @Override
    protected ButtonHolder getButtonHolder(View itemView, int pos) {
        return new EV3ButtonHolder(itemView, pos);
    }

    @Override
    protected int getButtonHolderLayout() {
        return R.layout.ev3_edit_button;
    }

    // controlElements
    private class EV3JoystickHolder extends ControlAdapter.JoystickHolder {
        //TODO test ports different

        public EV3JoystickHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radioRight = itemView.findViewById(R.id.radio_port_right);
            RadioGroup radioLeft = itemView.findViewById(R.id.radio_port_left);

            edit.addTextChangedListener(new MotorPowerWatcher(pos));
            radioRight.setOnCheckedChangeListener(new PortChangedListener(pos, 2));
            radioLeft.setOnCheckedChangeListener(new PortChangedListener(pos, 3));

            update(pos);
        }

        @Override
        public void update(int pos) {
            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radioRight = itemView.findViewById(R.id.radio_port_right);
            RadioGroup radioLeft = itemView.findViewById(R.id.radio_port_left);

            Integer t;
            t = elementValues.get(pos).get(1);
            if (t != null) {
                edit.setText(t.toString());
            } else {
                edit.setText("");
            }

            t = elementValues.get(pos).get(2);
            if (t != null) {
                radioRight.clearCheck();
                ((RadioButton) (radioRight.getChildAt(t))).setChecked(true);
            } else {
                radioRight.clearCheck();
            }

            t = elementValues.get(pos).get(3);
            if (t != null) {
                radioLeft.clearCheck();
                ((RadioButton) (radioLeft.getChildAt(t))).setChecked(true);
            } else {
                radioLeft.clearCheck();
            }
        }
    }

    private class EV3SliderHolder extends ControlAdapter.SliderHolder {

        public EV3SliderHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);

            edit.addTextChangedListener(new MotorPowerWatcher(pos));
            radio.setOnCheckedChangeListener(new PortChangedListener(pos, 2));

            update(pos);
        }

        @Override
        public void update(int pos) {
            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);

            Integer t;
            t = elementValues.get(pos).get(1);
            if (t != null) {
                edit.setText(t.toString());
            } else {
                edit.setText("");
            }

            t = elementValues.get(pos).get(2);
            if (t != null) {
                radio.clearCheck();
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
            } else {
                radio.clearCheck();
            }
        }
    }

    private class EV3ButtonHolder extends ControlAdapter.ButtonHolder {

        public EV3ButtonHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);
            EditText editDur = itemView.findViewById(R.id.edit_duration);

            edit.addTextChangedListener(new MotorPowerWatcher(pos));
            radio.setOnCheckedChangeListener(new PortChangedListener(pos, 2));
            editDur.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        setElementValue(pos, 3, Integer.parseInt(s.toString()));
                    } else {
                        removeElementValue(pos, 3);
                    }
                }
            });
        }

        @Override
        public void update(int pos) {
            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);
            EditText editDur = itemView.findViewById(R.id.edit_duration);

            Integer t;
            t = elementValues.get(pos).get(1);
            if (t != null) {
                edit.setText(t.toString());
            } else {
                edit.setText("");
            }

            t = elementValues.get(pos).get(2);
            if (t != null) {
                radio.clearCheck();
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
            } else {
                radio.clearCheck();
            }

            t = elementValues.get(pos).get(3);
            if (t != null) {
                editDur.setText(t.toString());
            } else {
                editDur.setText("");
            }
        }
    }

    // caps power to <=100 and saves it in elements
    private class MotorPowerWatcher implements TextWatcher {
        private final int pos, index;

        public MotorPowerWatcher(int pos) {
            this.pos = pos;
            this.index = 1;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                int value = Integer.parseInt(s.toString());
                if (value > 100) { // cap at 100%, input type doesn't allow <0
                    value = 100;
                    s.replace(0, s.length(), Integer.toString(value));
                }
                setElementValue(pos, index, value);
            } else {
                removeElementValue(pos, index);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    // saves index of selected port to elements
    private class PortChangedListener implements RadioGroup.OnCheckedChangeListener {
        private final int pos, index;

        public PortChangedListener(int pos, int index) {
            this.pos = pos;
            this.index = index;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, checkedId + " checked");
            if (checkedId == -1) {
                removeElementValue(pos, index);
            } else {
                Integer checkedIndex = null;
                for (int i = 0; i < 4; i++) {
                    if (group.findViewById(checkedId).equals(group.getChildAt(i)))
                        checkedIndex = i;
                }
                setElementValue(pos, index, checkedIndex);
            }
        }
    }
}
