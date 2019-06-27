package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;

public class TrackedEntityInstanceAdapter extends PagedListAdapter<TrackedEntityInstance, ListItemWithSyncHolder> {

    public TrackedEntityInstanceAdapter() {
        super(new DiffByIdItemCallback<>());
    }

    @NonNull
    @Override
    public ListItemWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithSyncHolder holder, int position) {
        TrackedEntityInstance trackedEntityInstance = getItem(position);
        List<TrackedEntityAttributeValue> values = trackedEntityInstance.trackedEntityAttributeValues();
        if (values != null) {
            if (values.size() > 0) {
                holder.title.setText(trackedEntityInstance.trackedEntityAttributeValues().get(0).value());
            }
            if (values.size() > 1) {
                holder.subtitle1.setText(trackedEntityInstance.trackedEntityAttributeValues().get(1).value());
            }
            if (values.size() > 2) {
                holder.subtitle2.setText(trackedEntityInstance.trackedEntityAttributeValues().get(2).value());
            }
        }
        holder.rightText.setText(DateFormatHelper.formatDate(trackedEntityInstance.created()));
        holder.icon.setImageResource(R.drawable.ic_person_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        holder.syncIcon.setImageResource(R.drawable.ic_sync);
        setBackgroundColor(R.color.colorAccent, holder.syncIcon);
    }
}
