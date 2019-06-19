package com.example.android.androidskeletonapp.data.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.jexl2.JexlEngine;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.RuleExpressionEvaluator;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleAttributeValue;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEnrollment;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableAttribute;
import org.hisp.dhis.rules.models.RuleVariableCalculatedValue;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.rules.models.RuleVariablePreviousEvent;
import org.hisp.dhis.rules.models.TriggerEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Flowable;

public class RuleEngineService {

    private static Flowable<RuleEngine> ruleEngineFlowable;
    private static RuleEngineContext ruleEngineContext;
    private D2 d2;
    private String stage;
    private JexlEngine jexlEngine;

    private enum evaluationType {
        ENROLLMENT, EVENT
    }

    private String programUid;
    private String eventUid;
    private String enrollmentUid;

    public Flowable<RuleEngine> cornfigure(D2 d2, String programUid, @Nullable String enrollmentUid,
                                           @Nullable String eventUid) {
        this.d2 = d2;
        this.programUid = programUid;
        this.enrollmentUid = enrollmentUid;
        this.eventUid = eventUid;


        jexlEngine = new JexlEngine();

        return Flowable.zip(
                getRuleVariables(),
                getRules(),
                getEvents(enrollmentUid),
                (ruleVariables, rules, events) -> RuleEngineContext.builder(new RuleExpressionEvaluator() {
                    @Nonnull
                    @Override
                    public String evaluate(@Nonnull String expression) {
                        return jexlEngine.createExpression(expression).evaluate(null).toString();
                    }
                })
                        .ruleVariables(ruleVariables)
                        .rules(rules)
                        .supplementaryData(new HashMap<>())
                        .calculatedValueMap(new HashMap<>())
                        .build().toEngineBuilder()
                        .triggerEnvironment(TriggerEnvironment.ANDROIDCLIENT)
                        .events(events)
                        .build()
        );
    }

    private Flowable<RuleEnrollment> ruleEnrollment() {
        return Flowable.fromCallable(() -> {
            Enrollment enrollment = d2.enrollmentModule().enrollments.uid(enrollmentUid).get();
            String ouCode = d2.organisationUnitModule().organisationUnits.uid(enrollment.organisationUnit()).get().code();
            Program program = d2.programModule().programs.uid(enrollment.program()).withAllChildren().get();
            List<String> programAttributesUids = getProgramTrackedEntityAttributesUids(program.programTrackedEntityAttributes());

            List<RuleAttributeValue> attributeValues = transformToRuleAttributeValues(
                    d2.trackedEntityModule().trackedEntityAttributeValues
                            .byTrackedEntityInstance().eq(enrollment.trackedEntityInstance())
                            .byTrackedEntityAttribute().in(programAttributesUids)
                            .get()
            );
            return RuleEnrollment.create(
                    enrollment.uid(),
                    enrollment.incidentDate(),
                    enrollment.enrollmentDate(),
                    RuleEnrollment.Status.valueOf(enrollment.status().name()),
                    enrollment.organisationUnit(),
                    ouCode,
                    attributeValues,
                    program.name()
            );
        });
    }

    private List<String> getProgramTrackedEntityAttributesUids(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        List<String> attrUids = new ArrayList<>();
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes)
            attrUids.add(programTrackedEntityAttribute.uid());
        return attrUids;
    }

    private List<RuleAttributeValue> transformToRuleAttributeValues(List<TrackedEntityAttributeValue> attributeValues) {
        List<RuleAttributeValue> ruleAttributeValues = new ArrayList<>();

        for (TrackedEntityAttributeValue attributeValue : attributeValues)
            ruleAttributeValues.add(
                    RuleAttributeValue.create(attributeValue.trackedEntityAttribute(), attributeValue.value())
            );

        return ruleAttributeValues;
    }

    private Flowable<List<RuleEvent>> getEvents(String enrollmentUid) {
        return Flowable.fromCallable(() -> d2.eventModule().events
                .byEnrollmentUid().eq(enrollmentUid)
                .byStatus().in(EventStatus.ACTIVE, EventStatus.COMPLETED)
                .get()).flatMapIterable(events -> events)
                .map(this::transformToRuleEvent)
                .toList().toFlowable();
    }

    private RuleEvent transformToRuleEvent(Event event) {
        String code = d2.organisationUnitModule().organisationUnits.uid(event.organisationUnit()).get().code();
        String stageName = d2.programModule().programStages.uid(event.programStage()).get().name();
        List<TrackedEntityDataValue> eventDataValue = d2.trackedEntityModule().trackedEntityDataValues
                .byEvent().eq(event.uid()).get();
        List<RuleDataValue> ruleDataValues = transformToRuleDataValue(event, eventDataValue);
        return RuleEvent.create(event.uid(),
                event.programStage(),
                RuleEvent.Status.valueOf(event.status().name()),
                event.eventDate(),
                event.dueDate(),
                event.organisationUnit(),
                code,
                ruleDataValues,
                stageName);
    }

    private List<RuleDataValue> transformToRuleDataValue(Event event, List<TrackedEntityDataValue> eventDataValues) {
        List<RuleDataValue> ruleDataValues = new ArrayList<>();
        for (TrackedEntityDataValue dataValue : eventDataValues) {
            ruleDataValues.add(
                    RuleDataValue.create(event.eventDate(), event.programStage(), dataValue.dataElement(), dataValue.value())
            );
        }
        return ruleDataValues;
    }

    private Flowable<List<Rule>> getRules() {
        return Flowable.fromCallable(() -> d2.programModule().programRules.byProgramUid().eq(programUid).withProgramRuleActions().get())
                .map(this::transformToRule);
    }

    private Flowable<List<RuleVariable>> getRuleVariables() {
        return Flowable.fromCallable(() -> d2.programModule().programRuleVariables.byProgramUid().eq(programUid).get())
                .map(this::transformToRuleVariable);
    }

    private List<Rule> transformToRule(List<ProgramRule> programRules) {
        List<Rule> rules = new ArrayList<>();
        for (ProgramRule rule : programRules) {
            List<RuleAction> ruleActions = transformToRuleAction(rule.programRuleActions());
            rules.add(
                    Rule.create(rule.programStage().uid(), rule.priority(), rule.condition(), ruleActions, rule.name())
            );
        }
        return rules;
    }

    private List<RuleAction> transformToRuleAction(List<ProgramRuleAction> programRuleActions) {
        List<RuleAction> ruleActions = new ArrayList<>();

        for (ProgramRuleAction pra : programRuleActions) {
            switch (pra.programRuleActionType()) {
                case HIDEFIELD:
                    String field = pra.dataElement() != null ? pra.dataElement().uid() : pra.trackedEntityAttribute().uid();
                    ruleActions.add(RuleActionHideField.create(pra.content(), field));
                    break;
            }
        }

        return ruleActions;
    }

    private List<RuleVariable> transformToRuleVariable(List<ProgramRuleVariable> programRuleVariables) {
        List<RuleVariable> ruleVariables = new ArrayList<>();
        for (ProgramRuleVariable prv : programRuleVariables) {

            RuleValueType mimeType = null;

            TrackedEntityAttribute attr = null;
            DataElement de = null;

            switch (prv.programRuleVariableSourceType()) {
                case TEI_ATTRIBUTE:
                    attr = d2.trackedEntityModule().trackedEntityAttributes.uid(prv.trackedEntityAttribute().uid()).get();
                    if (attr != null)
                        mimeType = convertType(attr.valueType());
                    break;
                case DATAELEMENT_CURRENT_EVENT:
                case DATAELEMENT_PREVIOUS_EVENT:
                case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                    de = d2.dataElementModule().dataElements.uid(prv.dataElement().uid()).get();
                    if (de != null)
                        mimeType = convertType(de.valueType());
                    break;
                default:
                    break;
            }

            switch (prv.programRuleVariableSourceType()) {
                case TEI_ATTRIBUTE:
                    ruleVariables.add(RuleVariableAttribute.create(attr.name(), attr.uid(), mimeType));
                case DATAELEMENT_CURRENT_EVENT:
                    ruleVariables.add(RuleVariableCurrentEvent.create(de.name(), de.uid(), mimeType));
                case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                    ruleVariables.add(RuleVariableNewestEvent.create(de.name(), de.uid(), mimeType));
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                    if (stage != null)
                        ruleVariables.add(RuleVariableNewestStageEvent.create(de.name(), de.uid(), stage, mimeType));
                case DATAELEMENT_PREVIOUS_EVENT:
                    ruleVariables.add(RuleVariablePreviousEvent.create(de.name(), de.uid(), mimeType));
                case CALCULATED_VALUE:
                    String variable = de != null ? de.uid() : attr.uid();
                    String name = de != null ? de.name() : attr.name();
                    ruleVariables.add(RuleVariableCalculatedValue.create(name, variable != null ? variable : "", mimeType));
                default:
                    throw new IllegalArgumentException("Unsupported variable " +
                            "source type: " + prv.programRuleVariableSourceType().name());
            }
        }

        return ruleVariables;
    }

    @NonNull
    private static RuleValueType convertType(@NonNull ValueType valueType) {
        if (valueType.isInteger() || valueType.isNumeric()) {
            return RuleValueType.NUMERIC;
        } else if (valueType.isBoolean()) {
            return RuleValueType.BOOLEAN;
        } else {
            return RuleValueType.TEXT;
        }
    }

}
