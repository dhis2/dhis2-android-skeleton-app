package com.example.android.androidskeletonapp.ui.main;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.SyncStatusHelper;
import com.example.android.androidskeletonapp.ui.d2_errors.D2ErrorActivity;
import com.example.android.androidskeletonapp.ui.data_sets.DataSetsActivity;
import com.example.android.androidskeletonapp.ui.data_sets.reports.DataSetReportsActivity;
import com.example.android.androidskeletonapp.ui.foreign_key_violations.ForeignKeyViolationsActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.search.TrackedEntityInstanceSearchActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.user.User;

import java.text.MessageFormat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        compositeDisposable = new CompositeDisposable();

        User user = getUser();
        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(String.format("Hi %s!", user.firstName()));

        inflateMainView();
        createNavigationView(user);
    }

    private User getUser() {
        return Sdk.d2().userModule().user.getWithoutChildren();
    }

    private User getUserFromCursor() {
        try (Cursor cursor = Sdk.d2().databaseAdapter().query("SELECT * FROM user;")) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return User.create(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void inflateMainView() {
        TextView notificator = findViewById(R.id.notificator);
        TextView syncMetadataText = findViewById(R.id.syncMetadataText);
        TextView syncDataText = findViewById(R.id.syncDataText);
        ProgressBar progressBar = findViewById(R.id.syncProgressBar);
        FloatingActionButton syncButton = findViewById(R.id.syncButton);
        FloatingActionButton syncDataButton = findViewById(R.id.syncDataButton);

        if (SyncStatusHelper.isMetadataSynced()) {
            syncButton.hide();
            syncMetadataText.setVisibility(View.GONE);
            TextView downloadedProgramsText = findViewById(R.id.programsDownloadedText);
            TextView downloadedDataSetsText = findViewById(R.id.dataSetsDownloadedText);
            downloadedProgramsText.setText(MessageFormat.format("{0} Programs",
                    Sdk.d2().programModule().programs.count()));
            downloadedDataSetsText.setText(MessageFormat.format("{0} Data sets",
                    Sdk.d2().dataSetModule().dataSets.count()));
            if (SyncStatusHelper.isDataSynced()) {
                syncDataButton.hide();
                syncDataText.setVisibility(View.GONE);
                TextView downloadedTeisText = findViewById(R.id.trackedEntityInstancesDownloadedText);
                TextView downloadedDataValuesText = findViewById(R.id.dataValuesDownloadedText);
                downloadedTeisText.setText(MessageFormat.format("{0} Tracked entity instances",
                        Sdk.d2().trackedEntityModule().trackedEntityInstances.byState()
                                .neq(State.RELATIONSHIP).count()));
                downloadedDataValuesText.setText(MessageFormat.format("{0} Data values",
                        Sdk.d2().dataValueModule().dataValues.count()));
            } else {
                syncDataText.setVisibility(View.VISIBLE);
                syncDataButton.show();
                syncDataButton.setOnClickListener(view -> {
                    view.setEnabled(Boolean.FALSE);
                    view.setVisibility(View.GONE);
                    syncDataText.setVisibility(View.GONE);
                    Snackbar.make(view, "Syncing data", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    notificator.setText(R.string.syncing_data);
                    progressBar.setVisibility(View.VISIBLE);
                    downloadData();
                });
            }
        } else {
            syncButton.setOnClickListener(view -> {
                view.setEnabled(Boolean.FALSE);
                view.setVisibility(View.GONE);
                syncMetadataText.setVisibility(View.GONE);
                Snackbar.make(view, "Syncing metadata", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                notificator.setText(R.string.syncing_metadata);
                progressBar.setVisibility(View.VISIBLE);
                syncMetadata();
            });
        }
    }

    private void createNavigationView(User user) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView firstName = headerView.findViewById(R.id.firstName);
        TextView email = headerView.findViewById(R.id.email);
        firstName.setText(user.firstName());
        email.setText(user.email());
    }

    private void syncMetadata() {
        compositeDisposable.add(Completable.fromCallable(() -> Sdk.d2().syncMetaData().call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ActivityStarter.startActivity(this, ProgramsActivity.class, false),
                        Throwable::printStackTrace));
    }

    private void downloadData() {
        compositeDisposable.add(Observable.defer(() -> Sdk.d2().trackedEntityModule()
                .downloadTrackedEntityInstances(10, false, false))
                .mergeWith(Observable.defer(() -> Sdk.d2().aggregatedModule().data().download()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> ActivityStarter.startActivity(this, TrackedEntityInstancesActivity.class, false))
                .doOnError(Throwable::printStackTrace)
                .subscribe());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navPrograms) {
            ActivityStarter.startActivity(this, ProgramsActivity.class,false);
        } else if (id == R.id.navTrackedEntities) {
            ActivityStarter.startActivity(this, TrackedEntityInstancesActivity.class,false);
        } else if (id == R.id.navTrackedEntitiesSearch) {
            ActivityStarter.startActivity(this, TrackedEntityInstanceSearchActivity.class,false);
        } else if (id == R.id.navDataSets) {
            ActivityStarter.startActivity(this, DataSetsActivity.class,false);
        } else if (id == R.id.navDataSetReports) {
            ActivityStarter.startActivity(this, DataSetReportsActivity.class,false);
        } else if (id == R.id.navD2Errors) {
            ActivityStarter.startActivity(this, D2ErrorActivity.class,false);
        } else if (id == R.id.navFKViolations) {
            ActivityStarter.startActivity(this, ForeignKeyViolationsActivity.class,false);
        } else if (id == R.id.navExit) {
            compositeDisposable.add(logOut(this));
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
