package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import java.util.Objects;

class OptionSetImageFieldHolder extends FieldHolder {

    private final ImageView optionImage;

    private String fieldUid;
    private String fieldCurrentValue;

    OptionSetImageFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.optionImage = itemView.findViewById(R.id.optionImage);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        if (fieldItem.getObjectStyle().color() != null) {
            String color = fieldItem.getObjectStyle().color().startsWith("#") ?
                    fieldItem.getObjectStyle().color() : "#" + fieldItem.getObjectStyle().color();
            itemView.setBackgroundColor(Color.parseColor(color));
        }

        if (fieldItem.getObjectStyle().icon() != null) {
            String iconName = fieldItem.getObjectStyle().icon().startsWith("ic_") ?
                    fieldItem.getObjectStyle().icon() : "ic_" + fieldItem.getObjectStyle().icon();
            int icon = itemView.getContext().getResources().getIdentifier(
                    iconName, "drawable", itemView.getContext().getPackageName());
            optionImage.setImageResource(icon);
        }

        //initial value
        setInitialValue(Objects.equals(fieldCurrentValue, fieldItem.getOptionCode()));

        itemView.setOnClickListener(v -> valueSavedListener.onValueSaved(fieldUid, fieldItem.getOptionCode()));
    }


    private void setInitialValue(boolean isSelected) {
        label.setTextColor(isSelected ? ContextCompat.getColor(itemView.getContext(), R.color.colorAccentAlt) : Color.BLACK);
    }

}
