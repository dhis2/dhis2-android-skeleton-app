package com.example.android.androidskeletonapp.data.service.forms;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.event.EventObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.SectionRenderingType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public class EventFormService {

    private D2 d2;
    private static EventFormService instance;
    private final Map<String, FormField> fieldMap;
    private EventObjectRepository eventRepository;
    private boolean isListingRendering;

    private EventFormService() {
        fieldMap = new LinkedHashMap<>();
    }

    public static EventFormService getInstance() {
        if (instance == null)
            instance = new EventFormService();

        return instance;
    }

    public boolean init(D2 d2, String eventUid, String programUid, String ouUid) {
        this.d2 = d2;
        ProgramStage programStage = d2.programModule().programStages()
                .byProgramUid().eq(programUid).one().blockingGet();
        String defaultOptionCombo = d2.categoryModule().categoryOptionCombos()
                .byDisplayName().eq("default").one().blockingGet().uid();
        try {
            if (eventUid == null)
                eventUid = d2.eventModule().events().blockingAdd(
                        EventCreateProjection.builder()
                                .attributeOptionCombo(defaultOptionCombo)
                                .programStage(programStage.uid())
                                .program(programUid)
                                .organisationUnit(ouUid)
                                .build()
                );
            eventRepository = d2.eventModule().events().uid(eventUid);
            if (eventRepository.blockingGet().eventDate() == null)
                eventRepository.setEventDate(new Date());
            return true;
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            return false;
        }
    }

    public void delete() {
        try {
            eventRepository.blockingDelete();
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public static void clear() {
        instance = null;
    }
}
