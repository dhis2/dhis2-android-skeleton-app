package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;
import com.example.android.androidskeletonapp.ui.tracker_import_conflicts.TrackerImportConflictsAdapter;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.text.MessageFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

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
        holder.title.setText(valueAt(values, 2));
        holder.subtitle1.setText(valueAt(values, 3));
        holder.subtitle2.setText(MessageFormat.format("{0} - {1}",
                valueAt(values, 1), valueAt(values, 0)));
        holder.rightText.setText(DateFormatHelper.formatDate(trackedEntityInstance.created()));
        holder.icon.setImageResource(R.drawable.ic_person_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(trackedEntityInstance.state(), holder.syncIcon);
        setConflicts(trackedEntityInstance.uid(), holder);
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, int index) {
        return values != null && values.size() > index ? values.get(index).value() : null;
    }

    private void setConflicts(String trackedEntityInstanceUid, ListItemWithSyncHolder holder) {
        TrackerImportConflictsAdapter adapter = new TrackerImportConflictsAdapter();
        holder.recyclerView.setAdapter(adapter);
        adapter.setTrackerImportConflicts(Sdk.d2().importModule().trackerImportConflicts
                .byTrackedEntityInstanceUid().eq(trackedEntityInstanceUid).get());
    }
}
