/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectOneField;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.FormService.getFormResourceByName;

public class FormDisplayMainPresenter extends BasePresenter implements FormDisplayContract.Presenter.MainPresenter {

    private final long mPatientID;
    private final String mEncountertype;
    private final String mFormname;
    private FormDisplayContract.View.MainView mFormDisplayView;
    private Patient mPatient;
    private FormPageAdapter mPageAdapter;

    public FormDisplayMainPresenter(FormDisplayContract.View.MainView mFormDisplayView, Bundle bundle, FormPageAdapter mPageAdapter) {
        this.mFormDisplayView = mFormDisplayView;
        this.mPatientID =(long) bundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        this.mPatient =new PatientDAO().findPatientByID(Long.toString(mPatientID));
        this.mEncountertype =(String)bundle.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
        this.mFormname = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);
        this.mPageAdapter = mPageAdapter;
        mFormDisplayView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // This method is intentionally empty
    }

    @Override
    public void createEncounter() {
        List<InputField> inputFields = new ArrayList<>();
        List<SelectOneField> radioGroupFields = new ArrayList<>();

        mFormDisplayView.enableSubmitButton(false);

        Encountercreate encountercreate=new Encountercreate();
        encountercreate.setPatient(mPatient.getUuid());
        encountercreate.setEncounterType(mEncountertype);

        List<Obscreate> observations=new ArrayList<>();

        SparseArray<Fragment> activefrag = mPageAdapter.getRegisteredFragments();
        boolean valid=true;
        for (int i = 0;i < activefrag.size();i++) {
            FormDisplayPageFragment formPageFragment=(FormDisplayPageFragment)activefrag.get(i);
            if(!formPageFragment.checkInputFields()) {
                valid=false;
                break;
            }

            inputFields.addAll(formPageFragment.getInputFields());
            radioGroupFields.addAll(formPageFragment.getSelectOneFields());
        }

        if(valid) {
            for (InputField input: inputFields) {
                if(input.getValue()!=-1.0) {
                    Obscreate obscreate = new Obscreate();
                    obscreate.setConcept(input.getConcept());
                    obscreate.setValue(String.valueOf(input.getValue()));
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(mPatient.getUuid());
                    observations.add(obscreate);
                }
            }

            for (SelectOneField radioGroupField : radioGroupFields) {
                if (radioGroupField.getChosenAnswer() != null) {
                    Obscreate obscreate = new Obscreate();
                    obscreate.setConcept(radioGroupField.getConcept());
                    obscreate.setValue(radioGroupField.getChosenAnswer().getConcept());
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(mPatient.getUuid());
                    observations.add(obscreate);
                }
            }

            encountercreate.setObservations(observations);
            encountercreate.setFormname(mFormname);
            encountercreate.setPatientId(mPatientID);
            encountercreate.setFormUuid(getFormResourceByName(mFormname).getUuid());
            encountercreate.setObslist();
            encountercreate.save();

            if(!mPatient.isSynced()) {
                mPatient.addEncounters(encountercreate.getId());
                new PatientDAO().updatePatient(mPatient.getId(),mPatient);
                ToastUtil.error("Patient not yet registered. Form data is saved locally " +
                        "and will sync when internet connection is restored. ");
                mFormDisplayView.enableSubmitButton(true);
            }
            else {
                new EncounterService().addEncounter(encountercreate, new DefaultResponseCallbackListener() {
                    @Override
                    public void onResponse() {
                        mFormDisplayView.enableSubmitButton(true);
                    }
                    @Override
                    public void onErrorResponse(String errorMessage) {
                        mFormDisplayView.showToast(errorMessage);
                        mFormDisplayView.enableSubmitButton(true);
                    }
                });
                mFormDisplayView.quitFormEntry();
            }
        }
        else {
            mFormDisplayView.enableSubmitButton(true);
        }
    }
}
