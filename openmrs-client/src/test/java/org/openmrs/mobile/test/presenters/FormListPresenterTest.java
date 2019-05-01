package org.openmrs.mobile.test.presenters;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.formlist.FormListContract;
import org.openmrs.mobile.activities.formlist.FormListPresenter;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.FormResource;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.FormService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest(FormService.class)
public class FormListPresenterTest extends ACUnitTestBase {

    @Mock
    private FormListContract.View view;

    @Mock
    private EncounterDAO encounterDAO;

    private FormListPresenter presenter;
    private final int patientId = 1;

    @Before
    public void setUp() {
        presenter = new FormListPresenter(view, patientId, encounterDAO);
        mockActiveAndroidContext();
    }

    @Test
    public void showResourceList_shouldLoadListWhenFormWithJsonValueReferenceIsPresent() {
        List<FormResource> formList = new ArrayList<>();

        formList.add(createExampleFormResourceWithoutResourceList("firstForm"));
        formList.add(createExampleFormResourceWithoutResourceList("secondForm"));
        formList.add(createExampleFormResourceWithResourceList("thirdForm", "json"));

        PowerMockito.mockStatic(FormService.class);
        when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        String jsonForms[] = {"thirdForm"};
        verify(view).showFormList(jsonForms);
    }

    @Test
    public void showResourceList_shouldNotLoadListWhenFormWithJsonValueReferenceIsNotPresent() {
        List<FormResource> formList = new ArrayList<>();

        formList.add(createExampleFormResourceWithoutResourceList("firstForm"));
        formList.add(createExampleFormResourceWithoutResourceList("secondForm"));
        formList.add(createExampleFormResourceWithResourceList("thirdForm", "notjson"));

        PowerMockito.mockStatic(FormService.class);
        when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        String jsonForms[] = {};
        verify(view).showFormList(jsonForms);
    }

    @Test
    public void listItemClicked_shouldStartFormDisplayActivityIfEncounterTypeForSpecifiedFormIsPresent() {
        final String formName = "someForm";
        final String encounterTypeUuid = "15789573219881759238790";
        final int clickedPosition = 0;

        List<FormResource> formList = new ArrayList<>();
        formList.add(createExampleFormResourceWithResourceList(formName, "json"));
        String childValueReference = formList.get(clickedPosition).getResourceList().get(0).getValueReference();

        PowerMockito.mockStatic(FormService.class);
        when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        EncounterType encounterType = new EncounterType();
        encounterType.setUuid(encounterTypeUuid);
        when(encounterDAO.getEncounterTypeByFormName(formName)).thenReturn(encounterType);

        presenter.listItemClicked(clickedPosition, formName);

        verify(view).startFormDisplayActivity(formName, (long) patientId, childValueReference, encounterType.getUuid());
    }

    @Test
    public void listItemClicked_shouldDisplayErrorIfEncounterTypeForSpecifiedFormIsNotPresent() {
        final String formName = "someForm";
        final String encounterTypeUuid = "15789573219881759238790";
        final int clickedPosition = 0;

        List<FormResource> formList = new ArrayList<>();
        formList.add(createExampleFormResourceWithResourceList(formName, "json"));
        PowerMockito.mockStatic(FormService.class);
        when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        EncounterType encounterType = new EncounterType();
        encounterType.setUuid(encounterTypeUuid);
        when(encounterDAO.getEncounterTypeByFormName(formName)).thenReturn(null);

        presenter.listItemClicked(clickedPosition, formName);

        verify(view).showError(contains(formName));
    }

    private FormResource createExampleFormResourceWithoutResourceList(String formName) {
        final String exampleJson = getExampleFormResourceJson(formName);
        FormResource formResource = new Gson().fromJson(exampleJson, FormResource.class);
        formResource.setValueReference(exampleJson);
        formResource.setResourcelist();
        return formResource;
    }

    private FormResource createExampleFormResourceWithResourceList(String formName, String resName) {
        final String exampleJson = getExampleFormResourceJson(formName);
        final String exampleJson1 = getExampleFormResourceJson(resName);
        FormResource formResource = new Gson().fromJson(exampleJson, FormResource.class);

        List<FormResource> formResources = new ArrayList<>();
        formResources.add(new Gson().fromJson(exampleJson1, FormResource.class));
        formResource.setResources(formResources);
        formResource.setResourcelist();

        formResource.setValueReference(exampleJson1);
        return formResource;
    }

    private String getExampleFormResourceJson(String name) {
        return  "{" +
                "\"display\":\"json\"," +
                "\"name\":\"" + name + "\"," +
                "\"valueReference\":\"" +
                    "{" +
                    "\\\"name\\\":\\\"Some Form\\\"," +
                    "\\\"uuid\\\":\\\"77174d67-954f-45c4-a782-d157e70d59f4\\\"" +
                    "}\"" +
                "}";
    }

}
