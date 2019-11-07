package com.example.android.androidskeletonapp.ui.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.hisp.dhis.android.core.common.CoreObject;

public class DiffByIdItemCallback<E extends CoreObject> extends DiffUtil.ItemCallback<E> {

    @Override
    public boolean areItemsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem.id().equals(newItem.id());
    }

    @Override
    public boolean areContentsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem == newItem;
    }
}
