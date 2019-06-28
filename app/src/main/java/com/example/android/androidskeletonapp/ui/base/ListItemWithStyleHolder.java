package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemWithStyleHolder extends BaseListItemHolder {

    public final FrameLayout cardFrame;
    public final CardView card;

    public ListItemWithStyleHolder(@NonNull View view) {
        super(view);
        cardFrame = view.findViewById(R.id.cardFrame);
        card = view.findViewById(R.id.card);
    }
}