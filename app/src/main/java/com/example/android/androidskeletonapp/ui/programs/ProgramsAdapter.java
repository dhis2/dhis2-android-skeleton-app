package com.example.android.androidskeletonapp.ui.programs;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.program.Program;

import java.text.MessageFormat;

public class ProgramsAdapter extends PagedListAdapter<Program, ProgramsAdapter.ProgramsHolder> {

    private static final DiffUtil.ItemCallback<Program> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Program>() {
                @Override
                public boolean areItemsTheSame(@NonNull Program oldItem,
                                               @NonNull Program newItem) {
                    return oldItem.uid().equals(newItem.uid());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Program oldItem,
                                                  @NonNull Program newItem) {
                    return oldItem == newItem;
                }
            };
    private final OnProgramSelectionListener programSelectionListener;

    ProgramsAdapter(OnProgramSelectionListener programSelectionListener) {
        super(DIFF_CALLBACK);
        this.programSelectionListener = programSelectionListener;
    }

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
        Program program = getItem(position);
        holder.programName.setText(program.displayName());
        holder.stages.setText(MessageFormat.format("{0} Program stages", program.programStages().size()));

        int colorWhite = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorWhite);
        holder.programIcon.setColorFilter(colorWhite);

        ObjectStyle style = program.style();
        if (style != null) {
            if (style.icon() == null) {
                int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
                holder.programIcon.setImageResource(0);
                holder.programIcon.setBackgroundColor(emptyColor);
            } else {
                String iconName = style.icon().startsWith("ic_") ? style.icon() : "ic_" + style.icon();
                int icon = holder.itemView.getContext().getResources().getIdentifier(
                        iconName, "drawable", holder.itemView.getContext().getPackageName());
                holder.programIcon.setImageResource(icon);
                int darkColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccentDark);
                holder.programIcon.setBackgroundColor(darkColor);
            }

            if (style.color() == null || style.color().length() == 4) {
                int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
                holder.programCardFrame.setBackgroundColor(emptyColor);
            } else {
                String color = style.color().startsWith("#") ? style.color() : "#" + style.color();
                int programColor = Color.parseColor(color);
                holder.programCardFrame.setBackgroundColor(programColor);
            }
        } else {
            int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
            holder.programIcon.setImageResource(0);
            holder.programIcon.setBackgroundColor(emptyColor);
            holder.programCardFrame.setBackgroundColor(emptyColor);
        }

        holder.itemView.setOnClickListener(view -> programSelectionListener.onProgramSelected(program.uid()));
    }
}
