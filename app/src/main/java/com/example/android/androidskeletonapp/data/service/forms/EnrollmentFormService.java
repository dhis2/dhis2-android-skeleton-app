package com.example.android.androidskeletonapp.data.service.forms;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.enrollmentForm.EnrollmentFormActivity;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import io.reactivex.Observable;

public class EnrollmentFormService {

    private final D2 d2 = Sdk.d2();
    private static EnrollmentFormService instance;

    public static EnrollmentFormService getInstance() {
        if (instance == null)
            instance = new EnrollmentFormService();

        return instance;
    }

    public static @Nullable Observable<Intent> saveToEnroll(String selectedProgram, String orgUnit, Context context){
        try {

            String teiType = getTeiType(selectedProgram);

            String newTeiInstanceUid;
            newTeiInstanceUid = Sdk.d2().trackedEntityModule().trackedEntityInstances().blockingAdd(
                    TrackedEntityInstanceCreateProjection.builder()
                            .organisationUnit(orgUnit)
                            .trackedEntityType(teiType)
                            .build()
            );

            String finalNewTeiInstanceUid = newTeiInstanceUid;
            return Sdk.d2().enrollmentModule().enrollments().add(
                    EnrollmentCreateProjection.builder()
                            .organisationUnit(orgUnit)
                            .program(selectedProgram)
                            .trackedEntityInstance(finalNewTeiInstanceUid)
                            .build()
            ).map(intent -> EnrollmentFormActivity.getFormActivityIntent(
                    context,
                    finalNewTeiInstanceUid,
                    selectedProgram,
                    EnrollmentFormActivity.FormType.CREATE
            )).toObservable();

        } catch (D2Error e) {
            return null;
        }
    }

    public static @Nullable Observable<Intent> enroll(String selectedProgram, String teiUid, String orgUnit, Context context){

        try {
            return Sdk.d2().enrollmentModule().enrollments().add(
                    EnrollmentCreateProjection.builder()
                            .organisationUnit(orgUnit)
                            .program(selectedProgram)
                            .trackedEntityInstance(teiUid)
                            .build()
            ).map(intent -> EnrollmentFormActivity.getFormActivityIntent(
                    context,
                    teiUid,
                    selectedProgram,
                    EnrollmentFormActivity.FormType.CREATE
            )).toObservable();

        } catch (Exception e) {
            return  null;
        }

    }
    public static String getTeiType(String programUid) {
        return Sdk.d2().programModule().programs().uid(programUid).get().map(program ->  program.trackedEntityType().uid() ).blockingGet();
    }

    public static Boolean hasEnrollments(String teiUid) {
        return !Sdk.d2().enrollmentModule().enrollments().byTrackedEntityInstance().eq(teiUid).blockingIsEmpty();
    }

    public String create(String teiUid, String programUid, String ouUid) {
        try {
            String enrollmentUid = d2.enrollmentModule().enrollments().blockingAdd(
                    EnrollmentCreateProjection.builder()
                            .organisationUnit(ouUid)
                            .program(programUid)
                            .trackedEntityInstance(teiUid)
                            .build()
            );
            EnrollmentObjectRepository enrollmentRepository = d2.enrollmentModule().enrollments().uid(enrollmentUid);
            enrollmentRepository.setEnrollmentDate(getNowWithoutTime());
            enrollmentRepository.setIncidentDate(getNowWithoutTime());
            return enrollmentUid;
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            return null;
        }
    }


    public void delete(String enrollmentUid) {
        try {
            d2.enrollmentModule()
                    .enrollments()
                    .uid(enrollmentUid)
                    .blockingDelete();
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public static void clear() {
        instance = null;
    }

    private Date getNowWithoutTime() {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

}
