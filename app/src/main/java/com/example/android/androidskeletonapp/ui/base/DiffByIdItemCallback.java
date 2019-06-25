package com.example.android.androidskeletonapp.ui.base;

import org.hisp.dhis.android.core.common.Model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class DiffByIdItemCallback<E extends Model> extends DiffUtil.ItemCallback<E> {

    @Override
    public boolean areItemsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem.id().equals(newItem.id());
    }

    @Override
    public boolean areContentsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem == newItem;
    }
}
