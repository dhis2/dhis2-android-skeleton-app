package com.example.android.androidskeletonapp.ui.tracker_import_conflicts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.SubListItemHolder;

import org.hisp.dhis.android.core.imports.TrackerImportConflict;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackerImportConflictsAdapter extends RecyclerView.Adapter<SubListItemHolder> {

    private List<TrackerImportConflict> trackerImportConflictList = new ArrayList<>();

    @NonNull
    @Override
    public SubListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_list_item, parent, false);
        return new SubListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubListItemHolder holder, int position) {
        TrackerImportConflict trackerImportConflict = trackerImportConflictList.get(position);
        holder.title.setText(trackerImportConflict.conflict());
        holder.rightText.setText(trackerImportConflict.value());
    }

    @Override
    public int getItemCount() {
        return trackerImportConflictList.size();
    }

    public void setTrackerImportConflicts(List<TrackerImportConflict> trackerImportConflicts) {
        this.trackerImportConflictList = trackerImportConflicts;
        notifyDataSetChanged();
    }
}