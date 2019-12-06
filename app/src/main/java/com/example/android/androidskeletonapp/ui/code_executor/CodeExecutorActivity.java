package com.example.android.androidskeletonapp.ui.code_executor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.android.androidskeletonapp.BuildConfig;
import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;

import java.io.File;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.AttributeHelper.MALARIA_CASE_TET_UID;

public class CodeExecutorActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView executingNotificator;
    private TextView resultNotificator;

    private Disposable disposable;

    private final int CAMERA_RQ = 0;
    private final int CAMERA_PERMISSION = 0;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == CAMERA_RQ && resultCode == RESULT_OK) {
            try {
                insertFileToAttribute();
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        }
    }

    private void requestCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri photoUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                getPictureFile());
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePicture, CAMERA_RQ);
    }

    private void takeAPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            requestCamera();
        }
    }

    private Single<String> executeCode() {
        return Single.defer(() -> {
            takeAPicture();

            return Single.just("Picture added!");
        });
    }
    private TrackedEntityInstance createTeiAndItsAttributes() throws D2Error {
        OrganisationUnit organisationUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                .one()
                .blockingGet();

        String teiUid = Sdk.d2().trackedEntityModule().trackedEntityInstances()
                .blockingAdd(
                        TrackedEntityInstanceCreateProjection.builder()
                                .organisationUnit(organisationUnit.uid())
                                .trackedEntityType(MALARIA_CASE_TET_UID)
                                .build()
                );

        Program program = Sdk.d2().programModule().programs()
                .byTrackedEntityTypeUid().eq(MALARIA_CASE_TET_UID)
                .one()
                .blockingGet();

        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = Sdk.d2().programModule()
                .programTrackedEntityAttributes()
                .byProgram().eq(program.uid())
                .blockingGet();

        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
            TrackedEntityAttribute value = Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                    .uid(programTrackedEntityAttribute.trackedEntityAttribute().uid())
                    .blockingGet();

            Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(value.uid(), teiUid)
                    .blockingSet(value.name());
        }

        return Sdk.d2().trackedEntityModule().trackedEntityInstances()
                .uid(teiUid).blockingGet();
    }

    @Exercise(
            exerciseNumber = "ex09b",
            version = 1,
            title = "Create a file resource and assign it to a TEI.",
            tips = "Create a new File, use the FileResourceDirectoryHelper to get the file cache resource directory" +
                    "and name it as tempFile.png",
            solutionBranch = "sol09b"
    )
    private File getPictureFile() {
        return null;
    }

    @Exercise(
            exerciseNumber = "ex09b",
            version = 1,
            title = "Create a file resource and assign it to a TEI.",
            tips = "Get the picture file, if exists, resize the file using the FileResizerHelper. " +
                    "Use the file resource module to create a FileResource by adding the file." +
                    "Save the returned file resource uid." +
                    "Use the tracked entity module to set the file resource uid to the tracked entity attribute value." +
                    "It is possible to use the AttributeHelper to get the tei image.",
            solutionBranch = "sol09b"
    )
    private void insertFileToAttribute() throws D2Error {
        TrackedEntityInstance trackedEntityInstance = createTeiAndItsAttributes();
        
        // TODO Solve the exercise here.
    }
}
