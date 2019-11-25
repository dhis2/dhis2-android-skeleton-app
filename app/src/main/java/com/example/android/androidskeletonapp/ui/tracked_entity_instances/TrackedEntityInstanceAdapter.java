package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;
import com.example.android.androidskeletonapp.ui.tracker_import_conflicts.TrackerImportConflictsAdapter;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.text.MessageFormat;
import java.util.List;

import static com.example.android.androidskeletonapp.data.service.AttributeHelper.attributePatientIdUid;
import static com.example.android.androidskeletonapp.data.service.AttributeHelper.attributePatientNameUid;
import static com.example.android.androidskeletonapp.data.service.AttributeHelper.attributeResidentInCatchmentAreaUid;
import static com.example.android.androidskeletonapp.data.service.AttributeHelper.attributeYearOfBirthUid;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

public class TrackedEntityInstanceAdapter extends PagedListAdapter<TrackedEntityInstance, ListItemWithSyncHolder> {

    private DataSource<?, TrackedEntityInstance> source;

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
        holder.title.setText(valueAt(values, attributePatientNameUid()));
        holder.subtitle1.setText(valueAt(values, attributePatientIdUid()));
        holder.subtitle2.setText(setSubtitle2(values));
        holder.rightText.setText(DateFormatHelper.formatDate(trackedEntityInstance.created()));
        holder.icon.setImageResource(R.drawable.ic_person_black_24dp);
        holder.delete.setVisibility(View.VISIBLE);
        holder.delete.setOnClickListener(view ->{
            try {
                Sdk.d2().trackedEntityModule().trackedEntityInstances().uid(trackedEntityInstance.uid()).blockingDelete();
                invalidateSource();
                notifyDataSetChanged();
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        });
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(trackedEntityInstance.state(), holder.syncIcon);
        setConflicts(trackedEntityInstance.uid(), holder);
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, String attributeUid) {
        for (TrackedEntityAttributeValue attributeValue : values) {
            if (attributeValue.trackedEntityAttribute().equals(attributeUid)) {
                return attributeValue.value();
            }
        }

        return null;
    }

    private String setSubtitle2(List<TrackedEntityAttributeValue> values) {
        String yearOfBirth = valueAt(values, attributeYearOfBirthUid());
        String residentInCatchmentArea = valueAt(values, attributeResidentInCatchmentAreaUid());
        if (yearOfBirth != null) {
            if (residentInCatchmentArea != null) {
                return MessageFormat.format("{0} - {1}", yearOfBirth, residentInCatchmentArea);
            } else {
                return yearOfBirth;
            }
        } else {
            if (residentInCatchmentArea != null) {
                return residentInCatchmentArea;
            } else {
                return null;
            }
        }
    }

    private void setConflicts(String trackedEntityInstanceUid, ListItemWithSyncHolder holder) {
        TrackerImportConflictsAdapter adapter = new TrackerImportConflictsAdapter();
        holder.recyclerView.setAdapter(adapter);
        adapter.setTrackerImportConflicts(Sdk.d2().importModule().trackerImportConflicts()
                .byTrackedEntityInstanceUid().eq(trackedEntityInstanceUid).blockingGet());
    }

    public void setSource(DataSource<?, TrackedEntityInstance> dataSource) {
        this.source = dataSource;
    }

    public void invalidateSource() {
        source.invalidate();
    }
}
