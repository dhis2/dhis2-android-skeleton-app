package com.example.android.androidskeletonapp.ui.foreign_key_violations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemHolder;

import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

public class ForeignKeyViolationsAdapter extends PagedListAdapter<ForeignKeyViolation, ListItemHolder> {

    private SimpleDateFormat dateFormat;

    ForeignKeyViolationsAdapter() {
        super(new DiffByIdItemCallback<>());
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
        ForeignKeyViolation fkViolation = getItem(position);
        holder.title.setText(fkViolation.notFoundValue());
        holder.subtitle1.setText(fkViolation.fromTable() + "." + fkViolation.fromColumn());
        holder.subtitle2.setText(fkViolation.toTable() + "." + fkViolation.toColumn());
        holder.rightText.setText(dateFormat.format(fkViolation.created()));
        holder.icon.setImageResource(R.drawable.ic_foreign_key_black_24dp);
    }
}
