package org.openmrs.mobile.test.presenters;

import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity;
import com.openmrs.android_sdk.library.models.EncounterType;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.formlist.FormListContract;
import org.openmrs.mobile.activities.formlist.FormListPresenter;
import com.openmrs.android_sdk.library.dao.EncounterDAO;
import org.openmrs.mobile.test.ACUnitTestBase;
import com.openmrs.android_sdk.utilities.FormService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;

@PrepareForTest(FormService.class)
@RunWith(PowerMockRunner.class)
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
        List<FormResourceEntity> formList = new ArrayList<>();

        formList.add(createExampleFormResourceWithoutResourceList("firstForm"));
        formList.add(createExampleFormResourceWithoutResourceList("secondForm"));
        formList.add(createExampleFormResourceWithResourceList("thirdForm", "json"));

        PowerMockito.mockStatic(FormService.class);
        Mockito.lenient().when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        String jsonForms[] = {"thirdForm"};
        verify(view).showFormList(jsonForms);
    }

    @Test
    public void showResourceList_shouldNotLoadListWhenFormWithJsonValueReferenceIsNotPresent() {
        List<FormResourceEntity> formList = new ArrayList<>();

        formList.add(createExampleFormResourceWithoutResourceList("firstForm"));
        formList.add(createExampleFormResourceWithoutResourceList("secondForm"));
        formList.add(createExampleFormResourceWithResourceList("thirdForm", "notjson"));

        PowerMockito.mockStatic(FormService.class);
        Mockito.lenient().when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        String jsonForms[] = {};
        verify(view).showFormList(jsonForms);
    }

    @Test
    public void listItemClicked_shouldStartFormDisplayActivityIfEncounterTypeForSpecifiedFormIsPresent() {
        final String formName = "someForm";
        final String encounterTypeUuid = "15789573219881759238790";
        final int clickedPosition = 0;

        List<FormResourceEntity> formList = new ArrayList<>();
        formList.add(createExampleFormResourceWithResourceList(formName, "json"));
        String childValueReference = formList.get(clickedPosition).getResources().get(0).getValueReference();

        PowerMockito.mockStatic(FormService.class);
        Mockito.lenient().when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        EncounterType encounterType = new EncounterType(EncounterType.VISIT_NOTE);
        encounterType.setUuid(encounterTypeUuid);
        Mockito.lenient().when(encounterDAO.getEncounterTypeByFormName(formName)).thenReturn(encounterType);

        presenter.listItemClicked(clickedPosition, formName);

        verify(view).startFormDisplayActivity(formName, (long) patientId, childValueReference, encounterType.getUuid());
    }

    @Test
    public void listItemClicked_shouldDisplayErrorIfEncounterTypeForSpecifiedFormIsNotPresent() {
        final String formName = "someForm";
        final String encounterTypeUuid = "15789573219881759238790";
        final int clickedPosition = 0;

        List<FormResourceEntity> formList = new ArrayList<>();
        formList.add(createExampleFormResourceWithResourceList(formName, "json"));
        PowerMockito.mockStatic(FormService.class);
        Mockito.lenient().when(FormService.getFormResourceList()).thenReturn(formList);

        presenter.loadFormResourceList();

        EncounterType encounterType = new EncounterType(EncounterType.VISIT_NOTE);
        encounterType.setUuid(encounterTypeUuid);
        Mockito.lenient().when(encounterDAO.getEncounterTypeByFormName(formName)).thenReturn(null);
        presenter.listItemClicked(clickedPosition, formName);
        verify(view).showError(contains(formName));
    }

    private FormResourceEntity createExampleFormResourceWithoutResourceList(String formName) {
        final String exampleJson = getExampleFormResourceJson(formName);
        FormResourceEntity formResourceEntity = new Gson().fromJson(exampleJson, FormResourceEntity.class);
        formResourceEntity.setValueReference(exampleJson);
        formResourceEntity.getResources();
        return formResourceEntity;
    }

    private FormResourceEntity createExampleFormResourceWithResourceList(String formName, String resName) {
        final String exampleJson = getExampleFormResourceJson(formName);
        final String exampleJson1 = getExampleFormResourceJson(resName);
        FormResourceEntity formResourceEntity = new Gson().fromJson(exampleJson, FormResourceEntity.class);

        List<FormResourceEntity> formResources = new ArrayList<>();
        formResources.add(new Gson().fromJson(exampleJson1, FormResourceEntity.class));
        formResourceEntity.setResources(formResources);

        formResourceEntity.setValueReference(exampleJson1);
        return formResourceEntity;
    }

    private String getExampleFormResourceJson(String name) {
        return "{" +
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
