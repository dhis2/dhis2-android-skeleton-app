package com.example.android.androidskeletonapp.ui.d2_errors;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class D2ErrorActivity extends ListActivity {

    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_d2_errors, R.id.d2Errors_toolbar, R.id.d2_errors_recycler_view);
        observeD2Errors();
    }

    private void observeD2Errors() {
        D2ErrorAdapter adapter = new D2ErrorAdapter();
        recyclerView.setAdapter(adapter);

        disposable = Single.just(Sdk.d2().maintenanceModule().d2Errors
                .getPaged(20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d2Errors -> {
                    d2Errors.observe(this, d2ErrorPagedList -> {
                        adapter.submitList(d2ErrorPagedList);
                        findViewById(R.id.d2_errors_notificator).setVisibility(
                                d2ErrorPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }

    }
}
