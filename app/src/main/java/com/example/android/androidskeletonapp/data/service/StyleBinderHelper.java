package com.example.android.androidskeletonapp.data.service;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.State;

import androidx.core.content.ContextCompat;

public class StyleBinderHelper {

    public static void bindStyle(ListItemWithStyleHolder holder, ObjectStyle style) {
        if (style != null) {
            if (style.icon() == null) {
                int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
                holder.icon.setImageResource(0);
                holder.icon.setBackgroundColor(emptyColor);
            } else {
                String iconName = style.icon().startsWith("ic_") ? style.icon() : "ic_" + style.icon();
                int icon = holder.itemView.getContext().getResources().getIdentifier(
                        iconName, "drawable", holder.itemView.getContext().getPackageName());
                holder.icon.setImageResource(icon);
                int darkColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccentDark);
                holder.icon.setBackgroundColor(darkColor);
                int colorWhite = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorWhite);
                holder.icon.setColorFilter(colorWhite);
            }

            if (style.color() == null || style.color().length() == 4) {
                int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
                holder.cardFrame.setBackgroundColor(emptyColor);
            } else {
                String color = style.color().startsWith("#") ? style.color() : "#" + style.color();
                int programColor = Color.parseColor(color);
                holder.cardFrame.setBackgroundColor(programColor);
            }
        } else {
            int emptyColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorEmpty);
            holder.icon.setImageResource(0);
            holder.icon.setBackgroundColor(emptyColor);
            holder.cardFrame.setBackgroundColor(emptyColor);
        }
    }

    public static void setBackgroundColor(int color, ImageView imageView) {
        int col = ContextCompat.getColor(imageView.getContext(), color);
        Drawable drawable = imageView.getBackground();
        drawable.setColorFilter(col, PorterDuff.Mode.ADD);
        imageView.setBackground(drawable);
    }

    public static void setState(State state, ImageView syncIcon) {
        if (state == null) {
            syncIcon.setVisibility(View.GONE);
        } else {
            syncIcon.setVisibility(View.VISIBLE);
            if (state.equals(State.TO_UPDATE) || state.equals(State.TO_POST)|| state.equals(State.TO_DELETE)) {
                syncIcon.setImageResource(R.drawable.ic_not_sync);
                setBackgroundColor(R.color.colorAccentAlt, syncIcon);
            } else if (state.equals(State.ERROR) || state.equals(State.WARNING)) {
                syncIcon.setImageResource(R.drawable.ic_sync_problem);
                setBackgroundColor(R.color.colorWarn, syncIcon);
            } else {
                syncIcon.setImageResource(R.drawable.ic_sync);
                setBackgroundColor(R.color.colorAccent, syncIcon);
            }
        }
    }
}
