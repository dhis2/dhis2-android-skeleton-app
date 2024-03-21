package com.example.android.androidskeletonapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.data.Sdk
import com.example.android.androidskeletonapp.data.service.ActivityStarter
import com.example.android.androidskeletonapp.data.service.LogOutService
import com.example.android.androidskeletonapp.data.service.SyncStatusHelper
import com.example.android.androidskeletonapp.ui.code_executor.CodeExecutorActivity
import com.example.android.androidskeletonapp.ui.d2_errors.D2ErrorActivity
import com.example.android.androidskeletonapp.ui.data_sets.DataSetsActivity
import com.example.android.androidskeletonapp.ui.data_sets.instances.DataSetInstancesActivity
import com.example.android.androidskeletonapp.ui.foreign_key_violations.ForeignKeyViolationsActivity
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.search.TrackedEntityInstanceSearchActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.domain.aggregated.data.AggregatedD2Progress
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress
import org.hisp.dhis.android.core.user.User
import java.text.MessageFormat

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var compositeDisposable: CompositeDisposable? = null
    private var syncMetadataButton: FloatingActionButton? = null
    private var syncDataButton: FloatingActionButton? = null
    private var uploadDataButton: FloatingActionButton? = null
    private var syncStatusText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var isSyncing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        compositeDisposable = CompositeDisposable()
        val user = user
        val greeting = findViewById<TextView>(R.id.greeting)
        greeting.text = String.format("Hi %s!", user!!.displayName())
        syncMetadataButton = findViewById(R.id.syncMetadataButton)
        syncDataButton = findViewById(R.id.syncDataButton)
        uploadDataButton = findViewById(R.id.uploadDataButton)
        syncStatusText = findViewById(R.id.notificator)
        progressBar = findViewById(R.id.syncProgressBar)

        syncMetadataButton.let {
            it?.setOnClickListener(View.OnClickListener { view: View? ->
                setSyncing()
                Snackbar.make(view!!, "Syncing metadata", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                syncStatusText.let {
                    it?.setText(R.string.syncing_metadata)
                }
                syncMetadata()
            })
        }

        syncDataButton.let {
            it?.setOnClickListener(View.OnClickListener { view: View? ->
                setSyncing()
                Snackbar.make(view!!, "Syncing data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                syncStatusText.let {
                    it?.setText(R.string.syncing_data)}
                downloadData()
            })
        }
        uploadDataButton.let {
            it?.setOnClickListener(View.OnClickListener { view: View? ->
                setSyncing()
                Snackbar.make(view!!, "Uploading data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                syncStatusText.let {
                    it?.setText(R.string.uploading_data)
                }
                uploadData()
            })
        }


        inflateMainView()
        createNavigationView(user)
    }

    override fun onResume() {
        super.onResume()
        updateSyncDataAndButtons()
    }

    private val user: User?
        private get() = Sdk.d2().userModule().user().blockingGet()
    private val userFromCursor: User?
        private get() {
            Sdk.d2().databaseAdapter().query("SELECT * FROM user;").use { cursor ->
                return if (cursor.count > 0) {
                    cursor.moveToFirst()
                    User.create(cursor)
                } else {
                    null
                }
            }
        }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (compositeDisposable != null) {
            compositeDisposable!!.clear()
        }
    }

    private fun inflateMainView() {

    }

    private fun setSyncing() {
        isSyncing = true
        progressBar!!.visibility = View.VISIBLE
        syncStatusText!!.visibility = View.VISIBLE
        updateSyncDataAndButtons()
    }

    private fun setSyncingFinished() {
        isSyncing = false
        progressBar!!.visibility = View.GONE
        syncStatusText!!.visibility = View.GONE
        updateSyncDataAndButtons()
    }

    private fun disableAllButtons() {
        setEnabledButton(syncMetadataButton, false)
        setEnabledButton(syncDataButton, false)
        setEnabledButton(uploadDataButton, false)
    }

    private fun enablePossibleButtons(metadataSynced: Boolean) {
        if (!isSyncing) {
            setEnabledButton(syncMetadataButton, true)
            if (metadataSynced) {
                setEnabledButton(syncDataButton, true)
                if (SyncStatusHelper.isThereDataToUpload()) {
                    setEnabledButton(uploadDataButton, true)
                }
            }
        }
    }

    private fun setEnabledButton(floatingActionButton: FloatingActionButton?, enabled: Boolean) {
        floatingActionButton!!.isEnabled = enabled
        floatingActionButton.alpha = if (enabled) 1.0f else 0.3f
    }

    private fun updateSyncDataAndButtons() {
        disableAllButtons()
        val programCount = SyncStatusHelper.programCount()
        val dataSetCount = SyncStatusHelper.dataSetCount()
        val trackedEntityInstanceCount = SyncStatusHelper.trackedEntityInstanceCount()
        val singleEventCount = SyncStatusHelper.singleEventCount()
        val dataValueCount = SyncStatusHelper.dataValueCount()
        enablePossibleButtons(programCount + dataSetCount > 0)
        val downloadedProgramsText = findViewById<TextView>(R.id.programsDownloadedText)
        val downloadedDataSetsText = findViewById<TextView>(R.id.dataSetsDownloadedText)
        val downloadedTeisText = findViewById<TextView>(R.id.trackedEntityInstancesDownloadedText)
        val singleEventsDownloadedText = findViewById<TextView>(R.id.singleEventsDownloadedText)
        val downloadedDataValuesText = findViewById<TextView>(R.id.dataValuesDownloadedText)
        downloadedProgramsText.text = MessageFormat.format("{0}", programCount)
        downloadedDataSetsText.text = MessageFormat.format("{0}", dataSetCount)
        downloadedTeisText.text = MessageFormat.format("{0}", trackedEntityInstanceCount)
        singleEventsDownloadedText.text = MessageFormat.format("{0}", singleEventCount)
        downloadedDataValuesText.text = MessageFormat.format("{0}", dataValueCount)
    }

    private fun createNavigationView(user: User?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navView)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        val headerView = navigationView.getHeaderView(0)
        val firstName = headerView.findViewById<TextView>(R.id.firstName)
        val email = headerView.findViewById<TextView>(R.id.email)
        firstName.text = user!!.firstName()
        email.text = user.email()
    }

    private fun syncMetadata() {
        compositeDisposable!!.add(downloadMetadata()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { obj: Throwable -> obj.printStackTrace() }
            .doOnComplete {
                setSyncingFinished()
                ActivityStarter.startActivity(
                    this,
                    ProgramsActivity.getProgramActivityIntent(this),
                    false
                )
            }
            .subscribe())
    }

    private fun downloadMetadata(): Observable<D2Progress> {
        return Sdk.d2().metadataModule().download()
    }

    private fun downloadData() {
        compositeDisposable!!.add(
            Observable.merge(
                downloadTrackedEntityInstances(),
                downloadSingleEvents(),
                downloadAggregatedData()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    setSyncingFinished()
                    ActivityStarter.startActivity(
                        this,
                        TrackedEntityInstancesActivity.getTrackedEntityInstancesActivityIntent(
                            this,
                            null
                        ),
                        false
                    )
                }
                .doOnError { obj: Throwable -> obj.printStackTrace() }
                .subscribe())
    }

    private fun downloadTrackedEntityInstances(): Observable<TrackerD2Progress> {
        return Sdk.d2().trackedEntityModule().trackedEntityInstanceDownloader()
            .limit(10).limitByOrgunit(false).limitByProgram(false).download()
    }

    private fun downloadSingleEvents(): Observable<TrackerD2Progress> {
        return Sdk.d2().eventModule().eventDownloader()
            .limit(10).limitByOrgunit(false).limitByProgram(false).download()
    }

    private fun downloadAggregatedData(): Observable<AggregatedD2Progress> {
        return Sdk.d2().aggregatedModule().data().download()
    }

    private fun uploadData() {
        compositeDisposable!!.add(
            Sdk.d2().fileResourceModule().fileResources().upload()
                .concatWith(Sdk.d2().trackedEntityModule().trackedEntityInstances().upload())
                .concatWith(Sdk.d2().dataValueModule().dataValues().upload())
                .concatWith(Sdk.d2().eventModule().events().upload())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { setSyncingFinished() }
                .doOnError { obj: Throwable -> obj.printStackTrace() }
                .subscribe())
    }

    private fun wipeData() {
        compositeDisposable!!.add(
            Observable
                .fromCallable {
                    Sdk.d2().wipeModule().wipeData()
                    "Done wipeData"
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { obj: Throwable -> obj.printStackTrace() }
                .doOnComplete { setSyncingFinished() }
                .subscribe())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.navPrograms) {
            ActivityStarter.startActivity(
                this,
                ProgramsActivity.getProgramActivityIntent(this),
                false
            )
        } else if (id == R.id.navTrackedEntities) {
            ActivityStarter.startActivity(
                this,
                TrackedEntityInstancesActivity.getTrackedEntityInstancesActivityIntent(this, null),
                false
            )
        } else if (id == R.id.navTrackedEntitiesSearch) {
            ActivityStarter.startActivity(
                this,
                TrackedEntityInstanceSearchActivity.getIntent(this),
                false
            )
        } else if (id == R.id.navDataSets) {
            ActivityStarter.startActivity(this, DataSetsActivity.getIntent(this), false)
        } else if (id == R.id.navDataSetInstances) {
            ActivityStarter.startActivity(this, DataSetInstancesActivity.getIntent(this), false)
        } else if (id == R.id.navD2Errors) {
            ActivityStarter.startActivity(this, D2ErrorActivity.getIntent(this), false)
        } else if (id == R.id.navFKViolations) {
            ActivityStarter.startActivity(this, ForeignKeyViolationsActivity.getIntent(this), false)
        } else if (id == R.id.navCodeExecutor) {
            ActivityStarter.startActivity(this, CodeExecutorActivity.getIntent(this), false)
        } else if (id == R.id.navWipeData) {
            syncStatusText!!.setText(R.string.wiping_data)
            wipeData()
        } else if (id == R.id.navExit) {
            compositeDisposable!!.add(LogOutService.logOut(this))
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        fun getMainActivityIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
