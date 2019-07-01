package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.common.ValueType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class FormAdapter extends RecyclerView.Adapter<FieldHolder> {

    private final int OPTIONSET = 98;
    private final int OPTIONSETIMAGE = 99;
    private final OnValueSaved valueSavedListener;
    private boolean isListingRendering = true;

    private List<FormField> fields;

    public FormAdapter(OnValueSaved valueSavedListener) {
        this.fields = new ArrayList<>();
        this.valueSavedListener = valueSavedListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public FieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == OPTIONSET) {
            return new OptionSetFieldHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_field_optionset, parent, false), valueSavedListener);
        } else if (viewType == OPTIONSETIMAGE) {
            return new OptionSetImageFieldHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_field_optionset_image, parent, false), valueSavedListener);
        } else
            switch (ValueType.values()[viewType]) {
                case DATE:
                    return new DateFieldHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_date_field, parent, false), valueSavedListener);
                case BOOLEAN:
                case TRUE_ONLY:
                    return new BooleanFieldHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_boolean_field, parent, false), valueSavedListener);
                default:
                    return new TextFieldHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_field, parent, false), valueSavedListener);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull FieldHolder holder, int position) {
        holder.bind(fields.get(position));
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    @Override
    public long getItemId(int position) {
        return fields.get(position).hashCode();
    }

    public void updateData(List<FormField> updates) {
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
                return fields.get(oldItemPosition).getUid().equals(updates.get(newItemPosition).getUid());
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


    @Override
    public int getItemViewType(int position) {
        if (fields.get(position).getOptionSetUid() != null && fields.get(position).getOptionSetUid() != null)
            if (isListingRendering)
                return OPTIONSET;
            else
                return OPTIONSETIMAGE;
        else
            return fields.get(position).getValueType().ordinal();


    }

    public void setListingRendering(boolean isListingRendering) {
        this.isListingRendering = isListingRendering;
    }

    public interface OnValueSaved {
        void onValueSaved(String fieldUid, String value);
    }
}
