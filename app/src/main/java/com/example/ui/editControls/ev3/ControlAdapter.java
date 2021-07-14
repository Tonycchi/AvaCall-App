package com.example.ui.editControls.ev3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.editControls.AddControlElementFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int
            EMPTY = 69,
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    private final FragmentActivity activity;
    private final ArrayList<Integer[]> elements;
    private int itemCount = 1,
            motorCount = 0;
    private boolean done = false;

    public ControlAdapter(FragmentActivity activity) {
        this.activity = activity;
        this.elements = new ArrayList<>();
    }

    public ControlAdapter(FragmentActivity activity, RobotModel model) {
        this.activity = activity;
        this.elements = new ArrayList<>();
        if (model != null) {
            initElements(model.specs);
            done = true;
        }
    }

    private void initElements(String specs) {
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

    public void addElement(int element) {
        int incr = (element == JOYSTICK) ? 2 : 1;
        if (motorCount + incr <= 4) {
            Integer[] e;
            if (element == SLIDER) e = new Integer[2+1];
            else e = new Integer[3+1];
            e[0] = element;

            elements.add(e);
            itemCount++;
            motorCount += incr;
            notifyItemInserted(elements.size() - 1);
            done = false;
        } else {
            Toast.makeText(activity, R.string.edit_controls_motor_alert, Toast.LENGTH_LONG).show();
        }
    }

    public void removeElement(int position) {
        if (position < elements.size()) {
            int element = elements.remove(position)[0];
            itemCount--;
            motorCount -= (element == JOYSTICK) ? 2 : 1;
            notifyItemRemoved(position);
            done = motorCount < 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= elements.size() && motorCount < 4)
            return ADD;
        else if (position >= elements.size())
            return EMPTY;
        return elements.get(position)[0] + (position << 16);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        int vtype = viewType & 0x0000ffff;
        int pos = (viewType & 0xffff0000) >> 16;

        switch (vtype) {
            case JOYSTICK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_joystick, parent, false);
                viewHolder = new EV3JoystickHolder(view, pos);
                break;
            case SLIDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_slider, parent, false);
                viewHolder = new EV3SliderHolder(view, pos);
                break;
            case BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_button, parent, false);
                viewHolder = new EV3ButtonHolder(view, pos);
                break;
            case EMPTY:
                view = new Space(parent.getContext());
                viewHolder = new EmptyHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_control_button, parent, false);
                viewHolder = new AddOption(view, this);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    class AddOption extends RecyclerView.ViewHolder {

        public AddOption(@NonNull View itemView, ControlAdapter adapter) {
            super(itemView);
            Button b = itemView.findViewById(R.id.add_control);

            b.setOnClickListener((v -> new AddControlElementFragment(adapter).show(
                    activity.getSupportFragmentManager(), AddControlElementFragment.TAG)));
        }
    }

    abstract class DeletableHolder extends RecyclerView.ViewHolder {

        public DeletableHolder(@NonNull View itemView) {
            super(itemView);
            ImageButton delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener((v -> removeElement(getAdapterPosition())));
        }
    }

    // base classes for any type of robot
    abstract class JoystickHolder extends DeletableHolder {

        public JoystickHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    abstract class SliderHolder extends DeletableHolder {

        public SliderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    abstract class ButtonHolder extends DeletableHolder {

        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // ev3
    class EV3JoystickHolder extends JoystickHolder {

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
    class EV3SliderHolder extends SliderHolder {

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
    class EV3ButtonHolder extends ButtonHolder {

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

    class EmptyHolder extends  RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
