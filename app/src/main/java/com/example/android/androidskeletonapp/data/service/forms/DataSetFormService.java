package com.example.android.androidskeletonapp.data.service.forms;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSetInstance;
import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository;

import java.util.List;

import io.reactivex.Flowable;

public class DataSetFormService {


    private static DataSetFormService instance;
    private D2 d2;
    private DataSetInstance dataSetInstance;

    public static DataSetFormService getInstance() {
        if (instance == null)
            instance = new DataSetFormService();
        return instance;
    }

    public boolean init(D2 d2, String dataSetUid,
                        String orgUnitUid, String periodId, String attrOptComb) {
        this.d2 = d2;
        this.dataSetInstance = d2.dataSetModule().dataSetInstances()
                .byDataSetUid().eq(dataSetUid)
                .byOrganisationUnitUid().eq(orgUnitUid)
                .byPeriod().eq(periodId)
                .byAttributeOptionComboUid().eq(attrOptComb)
                .one().blockingGet();
        return true;
    }

    public Flowable<List<FormField>> getDataSetFields() {
        if (d2 == null)
            return Flowable.error(
                    new NullPointerException("D2 is null. DataSetForm has not been initialized, use init() first."));
        else
            return Flowable.fromCallable(() ->
                    d2.dataSetModule().dataSets().withDataSetElements()
                            .uid(dataSetInstance.dataSetUid()).blockingGet().dataSetElements()).flatMapIterable(dataSetElements -> dataSetElements)
                    .flatMap(dataSetElement -> {

                        DataElement dataElement = d2.dataElementModule().dataElements().uid(dataSetElement.dataElement().uid()).blockingGet();
                        return d2.categoryModule().categoryOptionCombos()
                                .byCategoryComboUid().eq(dataElement.categoryComboUid()).get().toFlowable()
                                .flatMapIterable(categoryOptionCombos -> categoryOptionCombos)
                                .map(categoryOptionCombo -> {

                                    DataValueObjectRepository dataValueRepository = d2.dataValueModule().dataValues().value(
                                            dataSetInstance.period(),
                                            dataSetInstance.organisationUnitUid(),
                                            dataElement.uid(),
                                            categoryOptionCombo.uid(),
                                            dataSetInstance.attributeOptionComboUid()
                                    );

                                    String value = null;
                                    String optionCode = null;
                                    if (dataValueRepository.blockingExists())
                                        value = dataValueRepository.blockingGet().value();

                                    String formName = dataElement.displayName()+" - "+categoryOptionCombo.displayName();

                                    return new FormField(
                                            dataElement.uid() + "_" + categoryOptionCombo.uid(),
                                            dataElement.optionSetUid(),
                                            dataElement.valueType(),
                                            formName,
                                            value,
                                            optionCode,
                                            true,
                                            dataElement.style()
                                    );
                                });

                    }).toList().toFlowable();

    }

    public static void clear() {
        instance = null;
    }

}
