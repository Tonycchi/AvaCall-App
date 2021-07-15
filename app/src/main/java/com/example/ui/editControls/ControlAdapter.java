package com.example.ui.editControls;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int
            EMPTY = 69,
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    private static final String TAG = "ControlAdapter";
    protected final HostActivity hostActivity;
    protected final ArrayList<Integer[]> elementValues;
    protected int itemCount = 1;
    protected int motorCount = 0;
    protected int maxNumberOfMotors = 100;
    protected int fieldsFilled = 0, numberOfFields = 0;
    protected int id;

    public ControlAdapter(HostActivity hostActivity, RobotModel model) {
        this.hostActivity = hostActivity;
        this.elementValues = new ArrayList<>();
        if (model != null)
            this.id = model.id;
        else
            this.id = 0;
    }

    abstract boolean isReadyToSave();
    abstract List<Integer[]> getValues();

    public int getId() {
        return id;
    };
    public void resetFilled() {
        fieldsFilled = 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypeAndPosition) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        int viewType = viewTypeAndPosition & 0x0000ffff;
        int position = (viewTypeAndPosition & 0xffff0000) >> 16;

        switch (viewType) {
            case ADD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_control_button, parent, false);
                viewHolder = new AddControlElement(view, this);
                break;
            case JOYSTICK:
                view = LayoutInflater.from(parent.getContext()).inflate(getJoystickHolderLayout(), parent, false);
                viewHolder = getJoystickHolder(view, position);
                break;
            case SLIDER:
                view = LayoutInflater.from(parent.getContext()).inflate(getSliderHolderLayout(), parent, false);
                viewHolder = getSliderHolder(view, position);
                break;
            case BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(getButtonHolderLayout(), parent, false);
                viewHolder = getButtonHolder(view, position);
                break;
            case EMPTY:
                view = new Space(parent.getContext());
                viewHolder = new EmptyHolder(view);
                break;
            default:
                Log.e(TAG, "unknown viewType not");
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    /**
     * adds an element according to elementType
     * @param elementType type of element, see static constants in ControlAdapter.java
     */
    public void addElement(int elementType) {
        int newMotors, newFields;
        switch (elementType) {
            case JOYSTICK:
                newMotors = 2;
                newFields = 3;
                break;
            case SLIDER:
                newFields = 2;
            case BUTTON:
                newMotors = 1;
                newFields = 3;
                break;
            default:
                newMotors = 0;
                newFields = 0;
        }

        if (motorCount + newMotors <= maxNumberOfMotors) {
            // each element is represented by an array of attributes to be used in ui:
            // joystick: [this.JOYSTICK, max power, right port, left port] -> length 3+1
            // slider:   [this.SLIDER, max power, port] -> length 2+1
            // button:   [this.BUTTON, power, port, duration] -> length 3+1

            Integer[] e;
            if (elementType == SLIDER) e = new Integer[2 + 1];
            else e = new Integer[3 + 1];
            e[0] = elementType;

            elementValues.add(e);
            itemCount++;
            motorCount += newMotors;
            notifyItemInserted(elementValues.size() - 1);

            numberOfFields += newFields;
        } else {
            hostActivity.showToast(R.string.edit_controls_motor_alert);
        }
    }

    /**
     * removes element at position and changes itemCount accordingly
     * @param position of element to remove
     */
    public void removeElement(int position) {
        if (position < elementValues.size()) {
            int element = elementValues.remove(position)[0];
            itemCount--;
            motorCount -= (element == JOYSTICK) ? 2 : 1;
            fieldsFilled = numberOfFields -= (element == SLIDER) ? 2 : 3;
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= elementValues.size() && motorCount < maxNumberOfMotors)
            return ADD;
        else if (position >= elementValues.size())
            return EMPTY;
        return elementValues.get(position)[0] + (position << 16);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    protected abstract JoystickHolder getJoystickHolder(View itemView, int pos);

    protected abstract int getJoystickHolderLayout();

    protected abstract SliderHolder getSliderHolder(View itemView, int pos);

    protected abstract int getSliderHolderLayout();

    protected abstract ButtonHolder getButtonHolder(View itemView, int pos);

    protected abstract int getButtonHolderLayout();

    class AddControlElement extends RecyclerView.ViewHolder {

        public AddControlElement(@NonNull View itemView, ControlAdapter adapter) {
            super(itemView);
            Button addControlElementButton = itemView.findViewById(R.id.add_control);

            addControlElementButton.setOnClickListener((v -> new AddControlElementFragment(adapter).show(
                    hostActivity.getSupportFragmentManager(), AddControlElementFragment.TAG)));
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

    class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
