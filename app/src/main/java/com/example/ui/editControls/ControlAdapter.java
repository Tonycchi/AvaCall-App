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

public abstract class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ControlAdapter";

    static final int
            EMPTY = 69,
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    protected final HostActivity hostActivity;
    protected final ArrayList<Integer[]> elements;
    protected int itemCount = 1;
    protected int motorCount = 0;
    protected int maxNumberOfMotors = 100;

    public ControlAdapter(HostActivity hostActivity, RobotModel model) {
        this.hostActivity = hostActivity;
        this.elements = new ArrayList<>();
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

    public void addElement(int elementType){
        int numberOfMotorsAdded;
        switch(elementType){
            case JOYSTICK:
                numberOfMotorsAdded = 2;
                break;
            case ADD:
                numberOfMotorsAdded = 0;
                break;
            default:
                numberOfMotorsAdded = 1;
                break;
        }

        if (motorCount + numberOfMotorsAdded <= maxNumberOfMotors) {
            Integer[] e;
            if (elementType == SLIDER) e = new Integer[2+1];
            else e = new Integer[3+1];
            e[0] = elementType;

            elements.add(e);
            itemCount++;
            motorCount += numberOfMotorsAdded;
            notifyItemInserted(elements.size() - 1);
        } else {
            hostActivity.showToast(R.string.edit_controls_motor_alert);
        }
    }

    public void removeElement(int position){
        if (position < elements.size()) {
            int element = elements.remove(position)[0];
            itemCount--;
            motorCount -= (element == JOYSTICK) ? 2 : 1;
            notifyItemRemoved(position);
        }
    };

    @Override
    public int getItemViewType(int position) {
        if (position >= elements.size() && motorCount < maxNumberOfMotors)
            return ADD;
        else if (position >= elements.size())
            return EMPTY;
        return elements.get(position)[0] + (position << 16);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { }

    @Override
    public int getItemCount() {
        return itemCount;
    }

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
    protected abstract JoystickHolder getJoystickHolder(View itemView, int pos);
    protected abstract int getJoystickHolderLayout();

    abstract class SliderHolder extends DeletableHolder {

        public SliderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    protected abstract SliderHolder getSliderHolder(View itemView, int pos);
    protected abstract int getSliderHolderLayout();

    abstract class ButtonHolder extends DeletableHolder {

        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    protected abstract ButtonHolder getButtonHolder(View itemView, int pos);
    protected abstract int getButtonHolderLayout();


    class EmptyHolder extends  RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
