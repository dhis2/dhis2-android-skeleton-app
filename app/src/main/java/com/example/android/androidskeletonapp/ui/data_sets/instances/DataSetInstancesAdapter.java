package com.example.android.androidskeletonapp.ui.data_sets.instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;

import org.hisp.dhis.android.core.dataset.DataSetInstance;

import java.text.MessageFormat;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

public class DataSetInstancesAdapter extends PagedListAdapter<DataSetInstance, ListItemWithSyncHolder> {

    DataSetInstancesAdapter() {
        super(new DiffByIdItemCallback<>());
    }

    @NonNull
    @Override
    public ListItemWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ListItemWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithSyncHolder holder, int position) {
        DataSetInstance dataSetInstance = getItem(position);
        holder.title.setText(MessageFormat.format("{0} - {1}",
                dataSetInstance.period(), dataSetInstance.periodType().name()));
        holder.subtitle1.setText(dataSetInstance.organisationUnitDisplayName());
        holder.subtitle2.setText(dataSetInstance.attributeOptionComboDisplayName());
        holder.icon.setImageResource(R.drawable.ic_assignment_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        holder.rightText.setText(dataSetInstance.valueCount().toString());
        setState(dataSetInstance.state(), holder.syncIcon);
    }
}
