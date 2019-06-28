package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubListItemHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView rightText;

    public SubListItemHolder(@NonNull View view) {
        super(view);
        title = view.findViewById(R.id.subItemTitle);
        rightText = view.findViewById(R.id.subRightText);
    }
}