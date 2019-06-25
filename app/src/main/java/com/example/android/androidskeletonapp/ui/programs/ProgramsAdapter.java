package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;

import org.hisp.dhis.android.core.program.Program;

import java.text.MessageFormat;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

public class ProgramsAdapter extends PagedListAdapter<Program, ListItemWithStyleHolder> {

    private final OnProgramSelectionListener programSelectionListener;

    ProgramsAdapter(OnProgramSelectionListener programSelectionListener) {
        super(new DiffByIdItemCallback<>());
        this.programSelectionListener = programSelectionListener;
    }

    @NonNull
    @Override
    public ListItemWithStyleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_with_style, parent, false);
        return new ListItemWithStyleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithStyleHolder holder, int position) {
        Program program = getItem(position);
        holder.title.setText(program.displayName());
        holder.subtitle1.setText(MessageFormat.format("{0} Program stages", program.programStages().size()));
        StyleBinderHelper.bindStyle(holder, program.style());

        holder.itemView.setOnClickListener(view -> programSelectionListener.onProgramSelected(program.uid()));
    }
}
