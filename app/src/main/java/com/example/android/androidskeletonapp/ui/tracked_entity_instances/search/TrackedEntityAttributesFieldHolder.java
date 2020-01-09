package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackedEntityAttributesFieldHolder extends SearchFieldHolder {

    private final Spinner spinner;
    private List<TrackedEntityAttribute> attributeList;
    private String fieldUid;
    private String fieldCurrentValue;

    TrackedEntityAttributesFieldHolder(@NonNull View itemView, SearchFormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner();

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner() {
        attributeList = Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                .blockingGet();
        List<String> attributeListNames = new ArrayList<>();
        attributeListNames.add(label.getText().toString());
        for (TrackedEntityAttribute attribute : attributeList) {
            attributeListNames.add(attribute.displayName());
        }
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, attributeListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!attributeList.isEmpty() && attributeList.size() >= i && i >= 1) {
                    TrackedEntityAttribute attribute = attributeList.get(i - 1);
                    if (attribute != null) {
                        valueSavedListener.onValueSaved("Attribute", attributeList.get(i - 1).uid());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                valueSavedListener.onValueSaved(fieldUid, null);
            }
        });
    }

    private void setInitialValue(String selectedCode) {
        for (int i = 0; i < attributeList.size(); i++)
            if (Objects.equals(attributeList.get(i).code(), selectedCode))
                spinner.setSelection(i + 1);
    }

}
