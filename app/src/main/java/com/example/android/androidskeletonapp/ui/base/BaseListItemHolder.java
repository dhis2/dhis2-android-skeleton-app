package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

public class BaseListItemHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView subtitle1;
    public final ImageView icon;
    public final ImageButton delete;

    public BaseListItemHolder(@NonNull View view) {
        super(view);
        title = view.findViewById(R.id.itemTitle);
        subtitle1 = view.findViewById(R.id.itemSubtitle1);
        icon = view.findViewById(R.id.itemIcon);
        delete = view.findViewById(R.id.deleteButton);
    }
}