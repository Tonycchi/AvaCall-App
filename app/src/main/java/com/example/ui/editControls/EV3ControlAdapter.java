package com.example.ui.editControls;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
            numberOfFields = 0;
            fieldsFilled = 0;
        }
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
        //TODO get array from other class (robot or smth)

        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();
        for (String t : tmp) {
            list.add(t.split(":"));
        }

        int fields = 0;
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
                    fields += 3;
                    break;
                case "slider":
                    // get attributes out of string:
                    // $maxPower$;$port$
                    ports = new int[1];
                    ports[0] = portToIndex(Integer.parseInt(attrs[1]));
                    // and put into list:
                    elementValues.add(newList(SLIDER, maxPower, ports[0]));
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

        public EV3JoystickHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radioRight = itemView.findViewById(R.id.radio_port_right);
            RadioGroup radioLeft = itemView.findViewById(R.id.radio_port_left);

            edit.setImeOptions(EditorInfo.IME_ACTION_DONE);

            edit.addTextChangedListener(new MotorPowerWatcher(pos, edit));
            radioRight.setOnCheckedChangeListener(new PortChangedListener(radioLeft, pos, 2));
            radioLeft.setOnCheckedChangeListener(new PortChangedListener(radioRight, pos, 3));

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
            radioRight.clearCheck();
            if (t != null) {
                ((RadioButton) (radioRight.getChildAt(t))).setChecked(true);
            }

            t = elementValues.get(pos).get(3);
            radioLeft.clearCheck();
            if (t != null) {
                ((RadioButton) (radioLeft.getChildAt(t))).setChecked(true);
            }
        }
    }

    private class EV3SliderHolder extends ControlAdapter.SliderHolder {

        public EV3SliderHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);

            edit.setImeOptions(EditorInfo.IME_ACTION_DONE);

            edit.addTextChangedListener(new MotorPowerWatcher(pos, edit));
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
            radio.clearCheck();
            if (t != null) {
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
            }
        }
    }

    private class EV3ButtonHolder extends ControlAdapter.ButtonHolder {

        public EV3ButtonHolder(@NonNull View itemView, int pos) {
            super(itemView);

            EditText edit = itemView.findViewById(R.id.edit_max_power);
            RadioGroup radio = itemView.findViewById(R.id.radio_port);
            EditText editDur = itemView.findViewById(R.id.edit_duration);

            edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editDur.setImeOptions(EditorInfo.IME_ACTION_DONE);

            edit.addTextChangedListener(new MotorPowerWatcher(pos, edit));
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
                        edit.setError(null);
                    } else {
                        removeElementValue(pos, 3);
                        edit.setError("Wert fehlt."); //TODO dont hardcode maybe static context????
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
            radio.clearCheck();
            if (t != null) {
                ((RadioButton) (radio.getChildAt(t))).setChecked(true);
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
        private final EditText edit;

        public MotorPowerWatcher(int pos, EditText edit) {
            this.pos = pos;
            this.index = 1;
            this.edit = edit;
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
                edit.setError(null);
            } else {
                removeElementValue(pos, index);
                edit.setError("Wert fehlt.");
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
        private RadioGroup opposite;

        public PortChangedListener(int pos, int index) {
            this.pos = pos;
            this.index = index;
        }

        public PortChangedListener(RadioGroup opposite, int pos, int index) {
            this(pos, index);
            this.opposite = opposite;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == -1) {
                resetDisableOpposite(-1);
                removeElementValue(pos, index);
                ((RadioButton) group.getChildAt(3)).setError("Wert fehlt.");
            } else {
                Integer checkedIndex = null;
                for (int i = 0; i < 4; i++) {
                    if (group.findViewById(checkedId).equals(group.getChildAt(i)))
                        checkedIndex = i;
                }
                resetDisableOpposite(checkedIndex);
                setElementValue(pos, index, checkedIndex);
                ((RadioButton) group.getChildAt(3)).setError(null);
            }
        }

        private void resetDisableOpposite(int checked) {
            if (opposite == null) return;
            for (int i = 0; i < 4; i++) {
                opposite.getChildAt(i).setEnabled(true);
            }
            if (checked >= 0) opposite.getChildAt(checked).setEnabled(false);
        }
    }
}
