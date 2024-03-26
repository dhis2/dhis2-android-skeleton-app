package com.example.android.androidskeletonapp.data.service.forms;

import android.text.TextUtils;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;

public class EnrollmentFormService {

    private final D2 d2 = Sdk.d2();
    private static EnrollmentFormService instance;

    public static EnrollmentFormService getInstance() {
        if (instance == null)
            instance = new EnrollmentFormService();

        return instance;
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
