package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.util.Objects;

class TextFieldHolder extends FieldHolder {

    private final TextInputEditText editText;

    TextFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.editText = itemView.findViewById(R.id.inputEditText);
    }

    void bind(Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
            TrackedEntityAttributeValueObjectRepository> fieldItem) {
        super.bind(fieldItem);

        if (fieldItem.getRight() != null && fieldItem.getRight().exists())
            editText.setText(fieldItem.getRight().get().value());

        editText.setEnabled(!fieldItem.getMiddle().generated());

        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (!fieldItem.getRight().exists() || fieldItem.getRight().exists() && !Objects.equals(fieldItem.getRight().get().value(), editText.getText().toString()))
                    valueSavedListener.onValueSaved(fieldItem.getMiddle().uid(), editText.getText().toString());
            }
        });
    }
}
