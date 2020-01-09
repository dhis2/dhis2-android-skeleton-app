package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import java.util.ArrayList;
import java.util.List;

public class SearchFormAdapter extends RecyclerView.Adapter<SearchFieldHolder> {

    private final int ATTRIBUTE = 98;
    private final int PROGRAM = 99;

    private final OnValueSaved valueSavedListener;

    private List<FormField> fields;

    public SearchFormAdapter(OnValueSaved valueSavedListener) {
        this.fields = new ArrayList<>();
        this.valueSavedListener = valueSavedListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public SearchFieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ATTRIBUTE) {
            return new TrackedEntityAttributesFieldHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_field_optionset, parent, false), valueSavedListener);
        } else {
            return new ProgramFieldHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_field_optionset, parent, false), valueSavedListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFieldHolder holder, int position) {
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
        if (fields.get(position).getFormLabel().equals("Attribute"))
            return ATTRIBUTE;
        else
            return PROGRAM;
    }

    public interface OnValueSaved {
        void onValueSaved(String fieldUid, String value);
    }
}
