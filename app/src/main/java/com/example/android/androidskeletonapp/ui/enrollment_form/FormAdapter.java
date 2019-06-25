package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.ArrayList;
import java.util.List;

public class FormAdapter extends RecyclerView.Adapter<FieldHolder> {

    private final int OPTIONSET = 98;
    private final OnValueSaved valueSavedListener;
    private final FormType formType;

    private List<Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute, TrackedEntityAttributeValueObjectRepository>> fields;
    private List<Triple<ProgramStageDataElement, DataElement, TrackedEntityDataValueObjectRepository>> fieldsEvents;

    public enum FormType {
        ENROLLMENT, EVENT
    }

    public FormAdapter(OnValueSaved valueSavedListener, FormType formType) {
        if (formType == FormType.ENROLLMENT)
            fields = new ArrayList<>();
        else
            fieldsEvents = new ArrayList<>();
        this.valueSavedListener = valueSavedListener;
        this.formType = formType;
    }

    @NonNull
    @Override
    public FieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == OPTIONSET) {
            return new OptionSetFieldHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field_optionset, parent, false),
                    valueSavedListener);
        } else
            switch (ValueType.values()[viewType]) {
                case TEXT:
                case LONG_TEXT:
                    return new TextFieldHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field, parent, false),
                            valueSavedListener);
                default:
                    return new FieldHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field, parent, false),
                            valueSavedListener);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull FieldHolder holder, int position) {
        if (formType == FormType.ENROLLMENT)
            holder.bind(fields.get(position));
        else
            holder.bindEvents(fieldsEvents.get(position));
    }

    @Override
    public int getItemCount() {
        if (formType == FormType.ENROLLMENT)
            return fields.size();
        else
            return fieldsEvents.size();
    }

    public void updateData(List<Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute, TrackedEntityAttributeValueObjectRepository>> updates) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return fields.size();
            }

            @Override
            public int getNewListSize() {
                return updates.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return fields.get(oldItemPosition).getMiddle().uid().equals(updates.get(newItemPosition).getMiddle().uid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return fields.get(oldItemPosition) == updates.get(newItemPosition);
            }
        });

        fields.clear();
        fields.addAll(updates);

        diffResult.dispatchUpdatesTo(this);
    }

    public void updateDataEvents(List<Triple<ProgramStageDataElement, DataElement, TrackedEntityDataValueObjectRepository>> updates) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return fieldsEvents.size();
            }

            @Override
            public int getNewListSize() {
                return fieldsEvents.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return fieldsEvents.get(oldItemPosition).getMiddle().uid().equals(updates.get(newItemPosition).getMiddle().uid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return fieldsEvents.get(oldItemPosition) == updates.get(newItemPosition);
            }
        });

        fieldsEvents.clear();
        fieldsEvents.addAll(updates);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (formType == FormType.ENROLLMENT)
            if (fields.get(position).getMiddle().optionSet() != null && fields.get(position).getMiddle().optionSet().uid() != null)
                return OPTIONSET;
            else
                return fields.get(position).getMiddle().valueType().ordinal();
        else {
            if (fieldsEvents.get(position).getMiddle().optionSet() != null && fieldsEvents.get(position).getMiddle().optionSet().uid() != null)
                return OPTIONSET;
            else
                return fieldsEvents.get(position).getMiddle().valueType().ordinal();
        }

    }

    public interface OnValueSaved {
        void onValueSaved(String fieldUid, String value);
    }
}
