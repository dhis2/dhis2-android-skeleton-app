package com.example.android.androidskeletonapp.ui.data_sets.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemHolder;

import org.hisp.dhis.android.core.datavalue.DataSetReport;

import java.text.MessageFormat;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

public class DataSetReportsAdapter extends PagedListAdapter<DataSetReport, ListItemHolder> {

    DataSetReportsAdapter() {
        super(new DiffByIdItemCallback<>());
    }

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        DataSetReport dataSetReport = getItem(position);
        holder.title.setText(MessageFormat.format("{0} - {1}",
                dataSetReport.period(), dataSetReport.organisationUnitDisplayName()));
        holder.subtitle1.setText(dataSetReport.attributeOptionComboDisplayName());
        holder.subtitle2.setText(MessageFormat.format("{0} - {1}",
                dataSetReport.periodType().name(), dataSetReport.state().name()));
        holder.icon.setImageResource(R.drawable.ic_assignment_black_24dp);
        holder.rightText.setText(dataSetReport.valueCount().toString());
    }
}
