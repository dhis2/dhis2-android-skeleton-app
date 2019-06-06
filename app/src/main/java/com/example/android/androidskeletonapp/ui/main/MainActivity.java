package com.example.android.androidskeletonapp.ui.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.D2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView greeting = findViewById(R.id.greeting);
        TextView notificator = findViewById(R.id.notificator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ProgressBar progressBar = findViewById(R.id.sync_progress_bar);
        setSupportActionBar(toolbar);
        D2 d2 = Sdk.d2();

        FloatingActionButton syncButton = findViewById(R.id.sync_button);
        syncButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.GONE);
            Snackbar.make(view, "Syncing metadata", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            syncMetadata();
        });

        greeting.setText(String.format("Hi %s!", d2.userModule().user.getWithoutChildren().firstName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_item) {
            logOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncMetadata() {
        AsyncTask.execute(() -> {
            try {
                Sdk.d2().syncMetaData().call();

                Intent programsIntent = new Intent(getApplicationContext(), ProgramsActivity.class);
                startActivity(programsIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
