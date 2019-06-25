package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;

public class ListItemHolder extends BaseListItemHolder {

    public final TextView rightText;
    public final TextView subtitle2;

    public ListItemHolder(@NonNull View view) {
        super(view);
        rightText = view.findViewById(R.id.rightText);
        subtitle2 = view.findViewById(R.id.itemSubtitle2);
    }
}