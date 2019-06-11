package com.example.android.androidskeletonapp.ui.programs;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.program.Program;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ProgramsHolder> {
    private List<Program>  programs = new ArrayList<>();

    static class ProgramsHolder extends RecyclerView.ViewHolder {

        TextView programName;
        TextView stages;
        ImageView programIcon;
        FrameLayout programCardFrame;

        ProgramsHolder(@NonNull View view) {
            super(view);
            programName = view.findViewById(R.id.program_name);
            stages = view.findViewById(R.id.program_stages);
            programIcon = view.findViewById(R.id.program_icon);
            programCardFrame = view.findViewById(R.id.program_card_frame);
        }
    }

    @NonNull
    @Override
    public ProgramsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_item, parent, false);
        return new ProgramsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramsHolder holder, int position) {
        Program program = programs.get(position);
        holder.programName.setText(program.displayName());
        holder.stages.setText(MessageFormat.format("{0} Program stages", program.programStages().size()));

        int colorWhite = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorWhite);
        holder.programIcon.setColorFilter(colorWhite);

        ObjectStyle style = program.style();
        if (style != null) {
            if (style.icon() != null) {
                String iconName = style.icon().startsWith("ic_") ? style.icon() : "ic_" + style.icon();
                int icon = holder.itemView.getContext().getResources().getIdentifier(
                        iconName, "drawable", holder.itemView.getContext().getPackageName());
                holder.programIcon.setImageResource(icon);
            }

            if (style.color() != null) {
                String color = style.color().startsWith("#") ? style.color() : "#" + style.color();
                int programColor = (color.length() == 4) ?
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary) :
                        Color.parseColor(color);
                holder.programCardFrame.setBackgroundColor(programColor);
            }
        }
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    void setPrograms(List<Program> programs) {
        this.programs = programs;
        notifyDataSetChanged();
    }
}
