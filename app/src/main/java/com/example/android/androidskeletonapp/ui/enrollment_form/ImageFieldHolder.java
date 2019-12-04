package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.fileresource.FileResource;

import java.io.File;

import static android.text.TextUtils.isEmpty;

class ImageFieldHolder extends FieldHolder {

    private final ImageView imageView;
    private final View clearButton;
    @Nullable
    private final FormAdapter.OnImageSelectionClick imageSelectionListener;

    ImageFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener,
                     @Nullable FormAdapter.OnImageSelectionClick imageSelectionListener) {
        super(itemView, valueSavedListener);
        this.imageSelectionListener = imageSelectionListener;
        this.imageView = itemView.findViewById(R.id.image);
        this.clearButton = itemView.findViewById(R.id.imageClear);
    }

    @Override
    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        if (!isEmpty(fieldItem.getValue())) {
            FileResource file = Sdk.d2().fileResourceModule().fileResources().uid(fieldItem.getValue()).blockingGet();
            File imageFile = new File(file.path());
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.path());
                imageView.setImageBitmap(bitmap);
                clearButton.setVisibility(View.VISIBLE);
            }
        } else {
            clearButton.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_photo);
        }

        imageView.setOnClickListener(view -> {
                    if (imageSelectionListener != null) {
                        imageSelectionListener.onImageSelectionClick(fieldItem.getUid());
                    }
                }
        );

        clearButton.setOnClickListener(view -> {
            clearButton.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_photo);
            valueSavedListener.onValueSaved(fieldItem.getUid(), "");
        });
    }
}
