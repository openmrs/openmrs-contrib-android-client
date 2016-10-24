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

import android.app.Fragment;
import android.os.Bundle;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.Obscreate;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FormDisplayMainPresenter implements FormDisplayContract.Presenter.MainPresenter {

    private final long mPatientID;
    private final String mEncountertype;
    private final String mFormname;
    private FormDisplayContract.View.MainView mFormDisplayView;
    private Patient mPatient;
    private List<Fragment> mFragList = new ArrayList<>();
    private List<InputField> mInputlist;

    public FormDisplayMainPresenter(FormDisplayContract.View.MainView mFormDisplayView, Bundle bundle) {
        this.mFormDisplayView = mFormDisplayView;
        this.mInputlist = new ArrayList<>();
        this.mPatientID =(long) bundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        this.mPatient =new PatientDAO().findPatientByID(Long.toString(mPatientID));
        this.mEncountertype =(String)bundle.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
        this.mFormname = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);

        mFormDisplayView.setPresenter(this);
    }

    @Override
    public void start() {}

    @Override
    public void createEncounter() {
        Encountercreate encountercreate=new Encountercreate();
        encountercreate.setPatient(mPatient.getUuid());
        encountercreate.setEncounterType(mEncountertype);

        List<Obscreate> observations=new ArrayList<>();

        List<Fragment> activefrag=getActiveFragments();
        boolean valid=true;
        for (Fragment f:activefrag) {
            FormDisplayPageFragment formPageFragment=(FormDisplayPageFragment)f;
            if(!formPageFragment.checkfields()) {
                valid=false;
                break;
            }
            List<InputField> pageinputlist=formPageFragment.getInputFields();
            mInputlist.addAll(pageinputlist);
        }

        if(valid) {
            for (InputField input: mInputlist) {
                if(input.getValue()!=-1.0) {
                    Obscreate obscreate = new Obscreate();
                    obscreate.setConcept(input.getConcept());
                    obscreate.setValue(input.getValue());
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(mPatient.getUuid());
                    observations.add(obscreate);
                }
            }

            encountercreate.setObservations(observations);
            encountercreate.setFormname(mFormname);
            encountercreate.setPatientId(mPatientID);
            encountercreate.setObslist();
            encountercreate.save();

            if(!mPatient.isSynced()) {
                mPatient.addEncounters(encountercreate.getId());
                new PatientDAO().updatePatient(mPatient.getId(),mPatient);
                ToastUtil.error("Patient not yet registered. Form data is saved locally " +
                        "and will sync when internet connection is restored. ");
            }
            else
                new EncounterService().addEncounter(encountercreate);
            mFormDisplayView.quitFormEntry();
        }
    }

    @Override
    public void addFragment(Fragment fragment) {
        mFragList.add(fragment);
    }

    private List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<>();

        for(Fragment f : mFragList) {
            if(f != null) {
                if(f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }
}
