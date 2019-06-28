package com.example.android.androidskeletonapp.data.service.forms;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;

public class FormField {

    private final boolean isEditable;
    private String uid;
    private String optionSetUid;
    private ValueType valueType;
    private String formLabel;
    private String value;
    private String optionCode;
    private boolean editable;
    private ObjectStyle objectStyle;

    public FormField(String uid, String optionSetUid,
                     ValueType valueType, String formLabel,
                     String value, String optionCode,
                     boolean isEditable, ObjectStyle objectStyle) {
        this.uid = uid;
        this.optionSetUid = optionSetUid;
        this.valueType = valueType;
        this.formLabel = formLabel;
        this.value = value;
        this.optionCode = optionCode;
        this.isEditable = isEditable;
        this.objectStyle = objectStyle;
    }

    public ObjectStyle getObjectStyle() {
        return objectStyle;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public String getUid() {
        return uid;
    }

    public String getOptionSetUid() {
        return optionSetUid;
    }

    public String getFormLabel() {
        return formLabel;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public String getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }
}
