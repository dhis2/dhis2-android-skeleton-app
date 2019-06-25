package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

class FieldHolder extends RecyclerView.ViewHolder {

    final FormAdapter.OnValueSaved valueSavedListener;
    TextView label;

    FieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
            TrackedEntityAttributeValueObjectRepository> fieldItem) {
        label.setText(fieldItem.getMiddle().displayName());
    }

    void bindEvents(Triple<ProgramStageDataElement, DataElement,
            TrackedEntityDataValueObjectRepository> fieldItem) {
        label.setText(fieldItem.getMiddle().displayName());
    }
}
