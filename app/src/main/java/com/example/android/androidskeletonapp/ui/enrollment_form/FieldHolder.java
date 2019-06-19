package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

class FieldHolder extends RecyclerView.ViewHolder {

    private TextView label;

    FieldHolder(@NonNull View itemView) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
    }

    void bind(Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
            TrackedEntityAttributeValueObjectRepository> fieldItem) {
        label.setText(fieldItem.getMiddle().displayName());
    }
}
