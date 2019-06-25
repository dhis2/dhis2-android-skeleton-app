package com.example.android.androidskeletonapp.ui.d2_errors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByUidItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemHolder;

import org.hisp.dhis.android.core.maintenance.D2Error;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

public class D2ErrorAdapter extends PagedListAdapter<D2Error, ListItemHolder> {

    private SimpleDateFormat dateFormat;

    D2ErrorAdapter() {
        super(new DiffByUidItemCallback<>());
        this.dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);
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
        D2Error d2Error = getItem(position);
        holder.title.setText(d2Error.errorCode().toString());
        holder.subtitle1.setText(d2Error.errorDescription());
        holder.subtitle2.setText(d2Error.errorComponent().toString());
        holder.rightText.setText(dateFormat.format(d2Error.created()));
        holder.icon.setImageResource(R.drawable.ic_error_outline_black_24dp);
    }
}
