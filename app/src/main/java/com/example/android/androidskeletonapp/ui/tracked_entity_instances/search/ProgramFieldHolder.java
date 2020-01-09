package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramFieldHolder extends SearchFieldHolder {

    private final Spinner spinner;
    private List<Program> programList;
    private String fieldUid;
    private String fieldCurrentValue;

    ProgramFieldHolder(@NonNull View itemView, SearchFormAdapter.OnValueSaved valueSavedListener) {
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
        programList = Sdk.d2().programModule().programs()
                .byProgramType().eq(ProgramType.WITH_REGISTRATION)
                .blockingGet();
        List<String> programListNames = new ArrayList<>();
        programListNames.add(label.getText().toString());
        for (Program program : programList) {
            programListNames.add(program.displayName());
        }
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, programListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!programList.isEmpty() && programList.size() >= i && i >= 1) {
                    Program program = programList.get(i - 1);
                    if (program != null) {
                        valueSavedListener.onValueSaved("Program", programList.get(i - 1).uid());
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
        for (int i = 0; i < programList.size(); i++)
            if (Objects.equals(programList.get(i).code(), selectedCode))
                spinner.setSelection(i + 1);
    }

}
