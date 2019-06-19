package com.example.android.androidskeletonapp.data.service.forms;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;

public class EnrollmentFormService {

    private D2 d2;
    private EnrollmentObjectRepository enrollmentRepository;
    private static EnrollmentFormService instance;
    private final Map<String,
            Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute, TrackedEntityAttributeValueObjectRepository>> fieldMap;

    private EnrollmentFormService() {
        fieldMap = new HashMap<>();
    }

    public static EnrollmentFormService getInstance() {
        if (instance != null)
            instance = new EnrollmentFormService();

        return instance;
    }

    public boolean init(D2 d2, String teiUid, String programUid, String ouUid) {
        this.d2 = d2;
        try {
            String enrollmentUid = d2.enrollmentModule().enrollments.add(
                    EnrollmentCreateProjection.builder()
                            .organisationUnit(ouUid)
                            .program(programUid)
                            .trackedEntityInstance(teiUid)
                            .build()
            );
            enrollmentRepository = d2.enrollmentModule().enrollments.uid(enrollmentUid);
            return true;
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            return false;
        }
    }


    public Flowable<Map<String,
            Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
                    TrackedEntityAttributeValueObjectRepository>>> getEnrollmentFormFields() {
        if (d2 == null)
            return Flowable.error(
                    new NullPointerException("D2 is null. EnrollmentForm has not been initialized, use init() function.")
            );
        else
            return Flowable.fromCallable(() ->
                    d2.programModule().programs.uid(enrollmentRepository.get().uid())
                            .withAllChildren().get()
                            .programTrackedEntityAttributes()
            )
                    .flatMapIterable(programTrackedEntityAttributes -> programTrackedEntityAttributes)
                    .map(programAttribute -> {
                        Triple<ProgramTrackedEntityAttribute,
                                TrackedEntityAttribute,
                                TrackedEntityAttributeValueObjectRepository> field =
                                Triple.of(
                                        programAttribute,
                                        d2.trackedEntityModule().trackedEntityAttributes
                                                .uid(
                                                        programAttribute.trackedEntityAttribute().uid())
                                                .get(),
                                        d2.trackedEntityModule().trackedEntityAttributeValues
                                                .value(programAttribute.trackedEntityAttribute().uid(),
                                                        enrollmentRepository.get().trackedEntityInstance())
                                );


                        fieldMap.put(programAttribute.trackedEntityAttribute().uid(), field);
                        return programAttribute;
                    }).toList().toFlowable()
                    .map(list -> fieldMap);
    }

    public void saveCoordinates(double lat, double lon) {
        try {
            enrollmentRepository.setCoordinate(Coordinates.create(lat, lon));
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public void saveEnrollmentDate(Date enrollmentDate) {
        try {
            enrollmentRepository.setEnrollmentDate(enrollmentDate);
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public void saveEnrollmentIncidentDate(Date incidentDate) {
        try {
            enrollmentRepository.setIncidentDate(incidentDate);
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public String getEnrollmentUid() {
        return enrollmentRepository.get().uid();
    }

    public void delete() {
        try {
            enrollmentRepository.delete();
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public static void clear() {
        instance = null;
    }

}
