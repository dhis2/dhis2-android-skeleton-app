package com.example.android.androidskeletonapp.data.service.forms;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.event.EventObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.ProgramStage;

import java.util.Date;

public class EventFormService {

    private final D2 d2 = Sdk.d2();
    private static EventFormService instance;

    public static EventFormService getInstance() {
        if (instance == null)
            instance = new EventFormService();

        return instance;
    }

    public boolean create(String eventUid, String programUid, String ouUid) {
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
            EventObjectRepository eventRepository = d2.eventModule().events().uid(eventUid);
            if (eventRepository.blockingGet().eventDate() == null)
                eventRepository.setEventDate(new Date());
            return true;
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            return false;
        }
    }

    public void delete(String eventUid) {
        try {
            Sdk.d2().eventModule()
                    .events()
                    .uid(eventUid)
                    .blockingDelete();
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public static void clear() {
        instance = null;
    }
}
