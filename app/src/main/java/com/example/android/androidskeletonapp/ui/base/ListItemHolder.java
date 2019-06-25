package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView subtitle1;
    public final TextView rightText;
    public final TextView subtitle2;
    public final ImageView icon;

    public ListItemHolder(@NonNull View view) {
        super(view);
        title = view.findViewById(R.id.itemTitle);
        rightText = view.findViewById(R.id.rightText);
        subtitle1 = view.findViewById(R.id.itemSubtitle1);
        subtitle2 = view.findViewById(R.id.itemSubtitle2);
        icon = view.findViewById(R.id.itemIcon);
    }
}