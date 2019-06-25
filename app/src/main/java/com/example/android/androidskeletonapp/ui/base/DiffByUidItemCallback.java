package com.example.android.androidskeletonapp.ui.base;

import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class DiffByUidItemCallback<E extends ObjectWithUidInterface> extends DiffUtil.ItemCallback<E> {

    @Override
    public boolean areItemsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem.uid().equals(newItem.uid());
    }

    @Override
    public boolean areContentsTheSame(@NonNull E oldItem, @NonNull E newItem) {
        return oldItem == newItem;
    }
}
