package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

class SearchFieldHolder extends RecyclerView.ViewHolder {

    final SearchFormAdapter.OnValueSaved valueSavedListener;
    TextView label;

    SearchFieldHolder(@NonNull View itemView, SearchFormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(FormField fieldItem) {
        label.setText(fieldItem.getFormLabel());
    }
}
