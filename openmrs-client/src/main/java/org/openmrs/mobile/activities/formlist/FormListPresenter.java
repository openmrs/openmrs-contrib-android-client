package org.openmrs.mobile.activities.formlist;

import com.activeandroid.query.Select;

import org.openmrs.mobile.models.retrofit.EncounterType;
import org.openmrs.mobile.models.retrofit.FormResource;

import java.util.Iterator;
import java.util.List;

public class FormListPresenter implements FormListContract.Presenter {

    private static String[] FORMS = null;

    private FormListContract.View view;
    private Long patientId;
    private List<FormResource> formResourceList;

    public static List<FormResource> getFormResourceList(){
        return new Select()
                .from(FormResource.class)
                .execute();
    }

    public static EncounterType getEncounterType(String formname) {
        return new Select()
                .from(EncounterType.class)
                .where("display = ?", formname)
                .executeSingle();
    }

    public FormListPresenter(FormListContract.View view, long patientId) {
        this.view = view;
        this.patientId = patientId;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadFormResourceList();
    }

    @Override
    public void loadFormResourceList() {
        formResourceList =getFormResourceList();

        Iterator<FormResource> iterator= formResourceList.iterator();

        while (iterator.hasNext())
        {
            FormResource formResource = iterator.next();
            List<FormResource> valueRef = formResource.getResourceList();
            String valueRefString=null;
            for(FormResource resource:valueRef)
            {
                if(resource.getName().equals("json"))
                    valueRefString = resource.getValueReference();
            }
            if(valueRefString==null) {
                iterator.remove();
            }
        }

        int size= formResourceList.size();
        FORMS=new String [size];
        for (int i=0;i<size;i++)
        {
            FORMS[i]= formResourceList.get(i).getName();
        }
        view.showFormList(FORMS);
    }

    @Override
    public void listItemClicked(int position, String formName) {
        List<FormResource> valueRef = formResourceList.get(position).getResourceList();
        String valueRefString = null;
        for(FormResource resource:valueRef)
        {
            if(resource.getName().equals("json"))
                valueRefString=resource.getValueReference();
        }

        EncounterType encType = getEncounterType(FORMS[position]);
        String encounterType = encType.getUuid();
        view.startFormDisplayActivity(formName, patientId, valueRefString, encounterType);
    }

}
