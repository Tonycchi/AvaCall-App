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

import com.example.Constants;
import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int
            EMPTY = 69,
            ADD = 0,
            JOYSTICK = Constants.JOYSTICK,
            SLIDER = Constants.SLIDER,
            BUTTON = Constants.BUTTON;
    private static final String TAG = "ControlAdapter";
    protected final HostActivity hostActivity; // for context
    protected final List<List<Integer>> elementValues; // represents UI state
    protected int itemCount = 1; // number of UI items, not equal to number control elements
    protected int maxNumberElements = 4;
    protected int fieldsFilled = 0, numberOfFields = 0; // fieldsFilled==numberOfFields ==> all required data entered
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
    abstract List<List<Integer>> getValues();

    public int getId() {
        return id;
    }
    void removeFilledFields(int count) {
        fieldsFilled -= count;
        if (fieldsFilled < 0)
            fieldsFilled = 0;
    }
    void setElementValue(int position, int index, int value) {
        Log.d(TAG, "addElVal " + value);
        if (elementValues.get(position).get(index) == null) {
            Log.d(TAG, "what");
            fieldsFilled++;
        }
        elementValues.get(position).set(index, value);
    }
    void removeElementValue(int position, int index) {
        if (elementValues.get(position).get(index) != null) fieldsFilled--;
        elementValues.get(position).set(index, null);
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
        int newFields; // no. of data fields for each control element
        switch (elementType) {
            case JOYSTICK:
            case BUTTON:
                newFields = 3;
                break;
            case SLIDER:
                newFields = 2;
                break;
            default:
                newFields = 0;
        }

        if (elementValues.size() < maxNumberElements) {
            // each element is represented by an array of attributes to be used in ui:
            // joystick: [this.JOYSTICK, max power, right port, left port] -> length 3+1
            // slider:   [this.SLIDER, max power, port] -> length 2+1
            // button:   [this.BUTTON, power, port, duration] -> length 3+1

            if (elementType == SLIDER)
                elementValues.add(newList(elementType, null, null));
            else
                elementValues.add(newList(elementType, null, null, null));

            itemCount++;
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
            int element = elementValues.get(position).get(0);
            elementValues.get(position).clear();
            elementValues.remove(position);
            itemCount--;
            numberOfFields -= (element == SLIDER) ? 2 : 3;
            removeFilledFields((element == SLIDER) ? 2 : 3);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= elementValues.size() && elementValues.size() < maxNumberElements)
            return ADD; // last element in list is add button, if less than 4 elements
        else if (position >= elementValues.size())
            return EMPTY; // more than 4 -> show empty space

        // get viewType and position of UI item in one 32 bit int:
        return elementValues.get(position).get(0) + (position << 16); //
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeletableHolder) {
            DeletableHolder d = (DeletableHolder) holder;
            d.update(position);
        }
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

        public abstract void update(int pos);
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

    protected List<Integer> newList(Integer... values) {
        ArrayList<Integer> r = new ArrayList<>(values.length);
        Collections.addAll(r, values);
        return r;
    }
}
