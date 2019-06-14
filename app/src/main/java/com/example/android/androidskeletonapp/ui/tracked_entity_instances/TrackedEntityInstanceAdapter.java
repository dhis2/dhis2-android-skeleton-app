package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackedEntityInstanceAdapter extends
        RecyclerView.Adapter<TrackedEntityInstanceAdapter.TrackedEntityInstancesHolder> {
    private List<TrackedEntityInstance>  trackedEntityInstances = new ArrayList<>();

    static class TrackedEntityInstancesHolder extends RecyclerView.ViewHolder {

        TextView trackedEntityInstanceName;
        TextView enrollments;

        TrackedEntityInstancesHolder(@NonNull View view) {
            super(view);
            trackedEntityInstanceName = view.findViewById(R.id.tracked_entity_instance_name);
            enrollments = view.findViewById(R.id.tracked_entity_instance_enrollments);
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
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(position);
        holder.trackedEntityInstanceName.setText(trackedEntityInstance.trackedEntityType());
        holder.enrollments.setText(MessageFormat.format("{0} Enrollments",
                trackedEntityInstance.enrollments().size()));
    }

    @Override
    public int getItemCount() {
        return trackedEntityInstances.size();
    }

    void setTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances) {
        this.trackedEntityInstances = trackedEntityInstances;
        notifyDataSetChanged();
    }
}
