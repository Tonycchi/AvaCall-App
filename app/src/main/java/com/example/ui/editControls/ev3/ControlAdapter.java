package com.example.ui.editControls.ev3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rcvc.R;
import com.example.ui.editControls.AddControlElementFrgmt;

import java.util.ArrayList;

public class ControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    private int itemCount = 1;
    private FragmentActivity activity;
    private ArrayList<Integer> elements;

    public ControlAdapter(FragmentActivity activity) {
        this.activity = activity;
        this.elements = new ArrayList<>();
    }

    public void addElement(int element) {
        elements.add(element);
        itemCount++;
    }

    public void removeElement(int position) {
        if (position < elements.size()) {
            elements.remove(position);
            itemCount--;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= elements.size())
            return ADD;
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

    class JoystickHolder extends RecyclerView.ViewHolder {

        public JoystickHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class SliderHolder extends RecyclerView.ViewHolder {

        public SliderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ButtonHolder extends RecyclerView.ViewHolder {

        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
