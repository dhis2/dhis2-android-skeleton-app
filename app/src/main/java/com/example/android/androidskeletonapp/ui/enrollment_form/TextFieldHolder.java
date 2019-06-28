package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

class TextFieldHolder extends FieldHolder {

    private final TextInputEditText editText;

    TextFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.editText = itemView.findViewById(R.id.inputEditText);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        editText.setText(fieldItem.getValue());

        editText.setEnabled(fieldItem.isEditable());

        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (!Objects.equals(fieldItem.getValue(), editText.getText().toString()))
                    valueSavedListener.onValueSaved(fieldItem.getUid(), editText.getText().toString());
            }
        });
    }
}
