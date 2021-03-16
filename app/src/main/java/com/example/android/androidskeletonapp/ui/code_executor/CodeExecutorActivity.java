package com.example.android.androidskeletonapp.ui.code_executor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.D2;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CodeExecutorActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView executingNotificator;
    private TextView resultNotificator;

    private Disposable disposable;

    public static Intent getIntent(Context context) {
        return new Intent(context, CodeExecutorActivity.class);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_executor);
        Toolbar toolbar = findViewById(R.id.codeExecutorToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        executingNotificator = findViewById(R.id.codeExecutorNotificator);
        resultNotificator = findViewById(R.id.resultNotificator);
        progressBar = findViewById(R.id.codeExecutorProgressBar);
        FloatingActionButton codeExecutorButton = findViewById(R.id.codeExecutorButton);

        codeExecutorButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.INVISIBLE);
            Snackbar.make(view, "Executing...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            executingNotificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            resultNotificator.setVisibility(View.INVISIBLE);

            disposable = executeCode()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                executingNotificator.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                resultNotificator.setText(result);
                                resultNotificator.setVisibility(View.VISIBLE);
                                view.setEnabled(Boolean.TRUE);
                                view.setVisibility(View.VISIBLE);
                            },
                            error -> {
                                error.printStackTrace();
                            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Exercise(
            exerciseNumber = "ex04",
            version = 1,
            title = "Create a tracked entity instance, enroll to a program and set its program tracked entity attribute values.",
            tips = "Organisation unit module -> get one organisation unit." +
                    "Program module -> get the program by its uid" +
                    "Tracked entity module -> add a new tracked entity instance" +
                    "Enrollment module -> enroll the tracked entity instance to the program" +
                    "Program module -> get the program tracked entity attributes of the program." +
                    "Iterate the program tracked entity attributes. For each one, if mandatory:" +
                    "   Tracked entity module -> set a tracked entity attribute value." +

                    "Execute and check on the tracked entity instance list that the TEI has been created." +
                    "Use the app to sync the TEI" +
                    "Go to the web -> Tracker capture -> and search for your created TEI"

    )

    private Single<String> executeCode() {
        return Single.defer(() -> {
            String childProgramUid = "IpHINAT79UW";
            D2 d2 = Sdk.d2();

            // TODO

            return Single.just("Tracked entity instance created!");
        });
    }

}
