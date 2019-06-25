package com.example.android.androidskeletonapp.ui.d2_errors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.maintenance.D2Error;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class D2ErrorAdapter extends PagedListAdapter<D2Error, D2ErrorAdapter.D2ErrorsHolder> {

    private SimpleDateFormat dateFormat;

    private static final  DiffUtil.ItemCallback<D2Error> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<D2Error>() {
                @Override
                public boolean areItemsTheSame(@NonNull D2Error oldItem,
                                               @NonNull D2Error newItem) {
                    return oldItem.uid().equals(newItem.uid());
                }

                @Override
                public boolean areContentsTheSame(@NonNull D2Error oldItem,
                                                  @NonNull D2Error newItem) {
                    return oldItem == newItem;
                }
            };

    D2ErrorAdapter() {
        super(DIFF_CALLBACK);
        this.dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);
    }

    static class D2ErrorsHolder extends RecyclerView.ViewHolder {

        TextView errorCode;
        TextView errorDescription;
        TextView created;
        TextView component;

        D2ErrorsHolder(@NonNull View view) {
            super(view);
            errorCode = view.findViewById(R.id.d2ErrorCode);
            errorDescription = view.findViewById(R.id.d2ErrorDescription);
            created = view.findViewById(R.id.d2ErrorCreation);
            component = view.findViewById(R.id.d2ErrorComponent);
        }
    }

    @NonNull
    @Override
    public D2ErrorsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.d2_error_item, parent, false);
        return new D2ErrorsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull D2ErrorsHolder holder, int position) {
        D2Error d2Error = getItem(position);
        holder.errorCode.setText(d2Error.errorCode().toString());
        holder.errorDescription.setText(d2Error.errorDescription());
        holder.component.setText(d2Error.errorComponent().toString());
        holder.created.setText(dateFormat.format(d2Error.created()));
    }
}
