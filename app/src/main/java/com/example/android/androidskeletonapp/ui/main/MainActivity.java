package com.example.android.androidskeletonapp.ui.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.D2Factory;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.D2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView greeting = findViewById(R.id.greeting);
        TextView notificator = findViewById(R.id.notificator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        D2 d2 = D2Factory.getD2(getApplicationContext());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Syncing metadata", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                notificator.setText(R.string.syncing);
                syncMetadata();
            }
        });

        FloatingActionButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Log out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                logOut();
            }
        });

        greeting.setText(String.format("Hi %s!", d2.userModule().user.getWithoutChildren().firstName()));
    }

    private void syncMetadata() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    D2Factory.getD2(getApplicationContext()).syncMetaData().call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logOut() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    D2Factory.getD2(getApplicationContext()).wipeModule().wipeEverything();

                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
