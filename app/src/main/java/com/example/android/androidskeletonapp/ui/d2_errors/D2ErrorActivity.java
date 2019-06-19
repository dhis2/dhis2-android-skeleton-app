package com.example.android.androidskeletonapp.ui.d2_errors;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class D2ErrorActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_d2_errors);
        Toolbar toolbar = findViewById(R.id.d2Errors_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        observeD2Errors();
    }

    @Override
    public boolean onSupportNavigateUp() {
        ActivityStarter.startActivity(this, MainActivity.class);
        return true;
    }

    @Override
    public void onBackPressed() {
        ActivityStarter.startActivity(this, MainActivity.class);
    }

    private void observeD2Errors() {
        RecyclerView d2ErrorsRecyclerView = findViewById(R.id.d2_errors_recycler_view);
        d2ErrorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        D2ErrorAdapter adapter = new D2ErrorAdapter();
        d2ErrorsRecyclerView.setAdapter(adapter);

        compositeDisposable.add(Single.just(Sdk.d2().maintenanceModule().d2Errors
                .getPaged(20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d2Errors -> {
                    d2Errors.observe(this, d2ErrorPagedList -> {
                        adapter.submitList(d2ErrorPagedList);
                        findViewById(R.id.d2_errors_notificator).setVisibility(
                                d2ErrorPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
