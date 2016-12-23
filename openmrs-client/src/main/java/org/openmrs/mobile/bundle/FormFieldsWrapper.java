package org.openmrs.mobile.bundle;

import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectOneField;

import java.io.Serializable;
import java.util.List;

public class FormFieldsWrapper implements Serializable {

    private List<InputField> inputFields;
    private List<SelectOneField> selectOneFields;

    public FormFieldsWrapper(List<InputField> inputFields, List<SelectOneField> selectOneFields) {
        this.inputFields = inputFields;
        this.selectOneFields = selectOneFields;
    }

    public List<InputField> getInputFields() {
        return inputFields;
    }

    public void setInputFields(List<InputField> inputFields) {
        this.inputFields = inputFields;
    }

    public List<SelectOneField> getSelectOneFields() {
        return selectOneFields;
    }

    public void setSelectOneFields(List<SelectOneField> selectOneFields) {
        this.selectOneFields = selectOneFields;
    }
}
