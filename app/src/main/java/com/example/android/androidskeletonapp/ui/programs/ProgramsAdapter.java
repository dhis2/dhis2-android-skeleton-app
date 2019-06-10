package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

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

        ProgramsHolder(@NonNull View view) {
            super(view);
            programName = view.findViewById(R.id.program_name);
            stages = view.findViewById(R.id.program_stages);
            programIcon = view.findViewById(R.id.program_icon);
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

        if (program.style() != null && program.style().icon() != null) {
            String iconName = program.style().icon().startsWith("ic_") ?
                    program.style().icon() : "ic_" + program.style().icon();
            int icon = holder.itemView.getContext().getResources().getIdentifier(
                    iconName, "drawable", holder.itemView.getContext().getPackageName());
            holder.programIcon.setImageResource(icon);
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
