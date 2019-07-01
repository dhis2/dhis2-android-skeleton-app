package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

class DateFieldHolder extends FieldHolder {

    private final Button dateButton;

    DateFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.dateButton = itemView.findViewById(R.id.dateButton);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        try {
            dateButton.setText(fieldItem.getValue() != null ?
                    DateFormatHelper.formatSimpleDate(DateFormatHelper.parseSimpleDate(fieldItem.getValue())) :
                    itemView.getContext().getString(R.string.date_button_text));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateButton.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(itemView.getContext(), (datePicker, year, month, day) -> {
                valueSavedListener.onValueSaved(fieldItem.getUid(), getDate(year, month, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

    }

    private String getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return DateFormatHelper.formatSimpleDate(date);
    }
}
