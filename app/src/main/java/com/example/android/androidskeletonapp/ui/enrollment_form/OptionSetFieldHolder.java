package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class OptionSetFieldHolder extends FieldHolder {

    private final Spinner spinner;

    OptionSetFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
            TrackedEntityAttributeValueObjectRepository> fieldItem) {
        super.bind(fieldItem);

        List<Option> optionList = Sdk.d2().optionModule().options.byOptionSetUid().eq(fieldItem.getMiddle().optionSet().uid()).get();
        List<String> optionListNames = new ArrayList<>();
        optionListNames.add(label.getText().toString());
        for (Option option : optionList) optionListNames.add(option.displayName());
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, optionListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (!fieldItem.getRight().exists() || fieldItem.getRight().exists() && !Objects.equals(fieldItem.getRight().get().value(), optionList.get(i - 1).code()))
                        valueSavedListener.onValueSaved(fieldItem.getMiddle().uid(), optionList.get(i - 1).code());
                } else if(fieldItem.getRight().exists())
                    valueSavedListener.onValueSaved(fieldItem.getMiddle().uid(), null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //initial value
        if (fieldItem.getRight() != null && fieldItem.getRight().exists()) {
            String selectedCode = fieldItem.getRight().get().value();
            for (int i = 0; i < optionList.size(); i++)
                if (Objects.equals(optionList.get(i).code(), selectedCode))
                    spinner.setSelection(i + 1);
        }
    }
}
