package com.example.android.androidskeletonapp.ui.data_sets.instances;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;
import com.example.android.androidskeletonapp.ui.data_sets.instances.data_set_form.DataSetFormActivity;

import org.hisp.dhis.android.core.dataset.DataSetInstance;

import java.text.MessageFormat;

public class DataSetInstancesAdapter extends PagedListAdapter<DataSetInstance, ListItemWithSyncHolder> {

    private final AppCompatActivity activity;

    DataSetInstancesAdapter(AppCompatActivity activity) {
        super(new DiffByIdItemCallback<>());
        this.activity = activity;
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
        holder.rightText.setText(String.format("%s", dataSetInstance.valueCount()));
        setState(dataSetInstance.state(), holder.syncIcon);

        holder.itemView.setOnClickListener(view -> ActivityStarter.startActivity(
                activity,
                DataSetFormActivity.getFormActivityIntent(
                        activity,
                        dataSetInstance.dataSetUid(),
                        dataSetInstance.period(),
                        dataSetInstance.organisationUnitUid(),
                        dataSetInstance.attributeOptionComboUid()),
                false
        ));
    }
}
