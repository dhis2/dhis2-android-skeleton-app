package com.example.android.androidskeletonapp.ui.data_sets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;
import com.example.android.androidskeletonapp.ui.data_sets.instances.data_set_form.OnDataSetClick;

import org.hisp.dhis.android.core.dataset.DataSet;

public class DataSetsAdapter extends PagedListAdapter<DataSet, ListItemWithStyleHolder> {

    private final OnDataSetClick onDataSetClick;

    DataSetsAdapter(OnDataSetClick onDataSetClick) {
        super(new DiffByIdItemCallback<>());
        this.onDataSetClick = onDataSetClick;
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
        DataSet dataSet = getItem(position);
        holder.title.setText(dataSet.displayName());
        holder.subtitle1.setText(dataSet.periodType().name());
        StyleBinderHelper.bindStyle(holder, dataSet.style());
        holder.itemView.setOnClickListener(view -> {
            onDataSetClick.OnDataSetClick(dataSet.uid());
        });
    }
}
