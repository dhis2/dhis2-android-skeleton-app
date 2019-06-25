package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByUidItemCallback;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TrackedEntityInstanceAdapter extends
        PagedListAdapter<TrackedEntityInstance, TrackedEntityInstanceAdapter.TrackedEntityInstancesHolder> {

    public TrackedEntityInstanceAdapter() {
        super(new DiffByUidItemCallback<>());
    }

    static class TrackedEntityInstancesHolder extends RecyclerView.ViewHolder {

        TextView trackedEntityInstanceName;
        TextView uniqueId;

        TrackedEntityInstancesHolder(@NonNull View view) {
            super(view);
            trackedEntityInstanceName = view.findViewById(R.id.trackedEntityInstanceName);
            uniqueId = view.findViewById(R.id.uniqueId);
        }
    }

    @NonNull
    @Override
    public TrackedEntityInstancesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracked_entity_instance_item, parent, false);
        return new TrackedEntityInstancesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackedEntityInstancesHolder holder, int position) {
        TrackedEntityInstance trackedEntityInstance = getItem(position);
        if (trackedEntityInstance.trackedEntityAttributeValues().size() > 2) {
            holder.trackedEntityInstanceName.setText(
                    trackedEntityInstance.trackedEntityAttributeValues().get(1).value());
            holder.uniqueId.setText(
                    trackedEntityInstance.trackedEntityAttributeValues().get(0).value());
        }
    }
}
