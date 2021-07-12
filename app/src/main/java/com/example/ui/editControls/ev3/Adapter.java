package com.example.ui.editControls.ev3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rcvc.R;
import com.example.ui.URLDialogFragment;
import com.example.ui.editControls.AddControlElementFrgmt;
import com.example.ui.editControls.EditControlsFragment;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int
            ADD = 0,
            JOYSTICK = 1,
            SLIDER = 2,
            BUTTON = 3;
    private int itemCount = 2;
    private FragmentActivity activity;

    private Adapter() {}

    public Adapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return JOYSTICK;
        return ADD;
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
                viewHolder = new AddOption(view);
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

        public AddOption(@NonNull View itemView) {
            super(itemView);
            Button b = itemView.findViewById(R.id.add_control);

            b.setOnClickListener((v -> {
                new AddControlElementFrgmt().show(
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
