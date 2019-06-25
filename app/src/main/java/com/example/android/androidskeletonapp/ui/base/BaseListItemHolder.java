package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseListItemHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView subtitle1;
    public final ImageView icon;

    public BaseListItemHolder(@NonNull View view) {
        super(view);
        title = view.findViewById(R.id.itemTitle);
        subtitle1 = view.findViewById(R.id.itemSubtitle1);
        icon = view.findViewById(R.id.itemIcon);
    }
}