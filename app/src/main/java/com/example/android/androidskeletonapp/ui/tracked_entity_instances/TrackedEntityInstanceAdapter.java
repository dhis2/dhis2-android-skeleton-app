package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;

import org.hisp.dhis.android.core.common.State;
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
        holder.title.setText(valueAt(values, 0));
        holder.subtitle1.setText(valueAt(values, 1));
        holder.subtitle2.setText(valueAt(values, 2));
        holder.rightText.setText(DateFormatHelper.formatDate(trackedEntityInstance.created()));
        holder.icon.setImageResource(R.drawable.ic_person_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(trackedEntityInstance.state(), holder.syncIcon);
    }

    private void setState(State state, ImageView syncIcon) {
        if (state == null) {
            syncIcon.setVisibility(View.GONE);
        } else {
            syncIcon.setVisibility(View.VISIBLE);
             if (state.equals(State.TO_UPDATE) || state.equals(State.TO_POST)|| state.equals(State.TO_DELETE)) {
                syncIcon.setImageResource(R.drawable.ic_not_sync);
                setBackgroundColor(R.color.colorAccentAlt, syncIcon);
            } else if (state.equals(State.ERROR) || state.equals(State.WARNING)) {
                syncIcon.setImageResource(R.drawable.ic_sync_problem);
                setBackgroundColor(R.color.colorWarn, syncIcon);
            } else {
                syncIcon.setImageResource(R.drawable.ic_sync);
                setBackgroundColor(R.color.colorAccent, syncIcon);
            }
        }
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, int index) {
        return values != null && values.size() > index ? values.get(index).value() : null;
    }
}
