package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.ImageView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;

public class ListItemWithSyncHolder extends ListItemHolder {

    public final ImageView syncIcon;

    public ListItemWithSyncHolder(@NonNull View view) {
        super(view);
        syncIcon = view.findViewById(R.id.syncIcon);
    }
}