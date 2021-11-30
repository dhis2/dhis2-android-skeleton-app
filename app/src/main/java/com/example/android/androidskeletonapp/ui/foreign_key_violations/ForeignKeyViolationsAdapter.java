package com.example.android.androidskeletonapp.ui.foreign_key_violations;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemHolder;

import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;

public class ForeignKeyViolationsAdapter extends PagedListAdapter<ForeignKeyViolation, ListItemHolder> {

    ForeignKeyViolationsAdapter() {
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
        ForeignKeyViolation fkViolation = getItem(position);
        holder.title.setText(fkViolation.notFoundValue());
        holder.subtitle1.setText(String.format("%s.%s", fkViolation.fromTable(), fkViolation.fromColumn()));
        holder.subtitle2.setText(String.format("%s.%s", fkViolation.toTable(), fkViolation.toColumn()));
        holder.rightText.setText(DateFormatHelper.formatDate(fkViolation.created()));
        holder.icon.setImageResource(R.drawable.ic_foreign_key_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
    }
}
