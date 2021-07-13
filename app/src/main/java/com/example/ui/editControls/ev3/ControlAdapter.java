package com.example.ui.editControls.ev3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rcvc.R;
import com.example.ui.editControls.AddControlElementFrgmt;

import java.util.ArrayList;

public class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int
            EMPTY = -1,
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    private final FragmentActivity activity;
    private final ArrayList<Integer> elements;
    private int itemCount = 1,
            motorCount = 0;

    public ControlAdapter(FragmentActivity activity) {
        this.activity = activity;
        this.elements = new ArrayList<>();
    }

    public void addElement(int element) {
        int incr = (element == JOYSTICK) ? 2 : 1;
        if (motorCount + incr <= 4) {
            elements.add(element);
            itemCount++;
            motorCount += incr;
            notifyItemInserted(elements.size() - 1);
        } else {
            Toast.makeText(activity, R.string.edit_controls_motor_alert, Toast.LENGTH_LONG).show();
        }
    }

    public void removeElement(int position) {
        if (position < elements.size()) {
            int element = elements.remove(position);
            itemCount--;
            motorCount -= (element == JOYSTICK) ? 2 : 1;
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= elements.size() && motorCount < 4)
            return ADD;
        else if (position >= elements.size())
            return EMPTY;
        return elements.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case JOYSTICK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_joystick, parent, false);
                viewHolder = new JoystickHolder(view);
                break;
            case SLIDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_slider, parent, false);
                viewHolder = new SliderHolder(view);
                break;
            case BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ev3_button, parent, false);
                viewHolder = new ButtonHolder(view);
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

            b.setOnClickListener((v -> {
                new AddControlElementFrgmt(adapter).show(
                        activity.getSupportFragmentManager(), AddControlElementFrgmt.TAG);
            }));
        }
    }

    abstract class DeletableHolder extends RecyclerView.ViewHolder {

        public DeletableHolder(@NonNull View itemView) {
            super(itemView);
            ImageButton delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener((v -> {
                removeElement(getAdapterPosition());
            }));
        }
    }

    class JoystickHolder extends DeletableHolder {

        public JoystickHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class SliderHolder extends DeletableHolder {

        public SliderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ButtonHolder extends DeletableHolder {

        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class EmptyHolder extends  RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
