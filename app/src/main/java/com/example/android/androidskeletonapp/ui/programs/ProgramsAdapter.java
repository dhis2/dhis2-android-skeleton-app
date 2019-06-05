package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.program.Program;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ProgramsHolder> {
    private List<Program>  programs = new ArrayList<>();

    public static class ProgramsHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ProgramsHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.program_name);
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
        holder.textView.setText(program.displayName());
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
        notifyDataSetChanged();
    }
}
