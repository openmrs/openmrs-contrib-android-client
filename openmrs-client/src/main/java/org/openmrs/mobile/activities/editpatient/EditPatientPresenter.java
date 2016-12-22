package org.openmrs.mobile.activities.editpatient;

import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.StringUtils;

public class EditPatientPresenter implements EditPatientContract.Presenter{

    private final EditPatientContract.View mEditPatientView;

    private Patient mPatient;
    private String mID;

    public EditPatientPresenter(String id, EditPatientContract.View mEditPatientView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mID = id;
        this.mEditPatientView = mEditPatientView;
        this.mEditPatientView.setPresenter(this);
    }

    @Override
    public void start(){}

    @Override
    public void confirm(Patient patient) {
        if(validate(patient)) {
            editPatient();
            mEditPatientView.setProgressBarVisibility(true);
            mEditPatientView.hideSoftKeys();
        } else {
            mEditPatientView.scrollToTop();
        }
    }

    private boolean validate(Patient patient) {

        boolean ferr=false, lerr=false, doberr=false, gerr=false, adderr=false;
        mEditPatientView.setErrorsVisibility(ferr, lerr, doberr, gerr, adderr);

        // Validate names
        if(StringUtils.isBlank(patient.getPerson().getName().getGivenName())) {
            ferr=true;
        }
        if(StringUtils.isBlank(patient.getPerson().getName().getFamilyName())) {
            lerr=true;
        }

        // Validate date of birth
        if(StringUtils.isBlank(patient.getPerson().getBirthdate())) {
            doberr = true;
        }

        // Validate address
        if(StringUtils.isBlank(patient.getPerson().getAddress().getAddress1())
                && StringUtils.isBlank(patient.getPerson().getAddress().getAddress2())
                && StringUtils.isBlank(patient.getPerson().getAddress().getCityVillage())
                && StringUtils.isBlank(patient.getPerson().getAddress().getStateProvince())
                && StringUtils.isBlank(patient.getPerson().getAddress().getCountry())
                && StringUtils.isBlank(patient.getPerson().getAddress().getPostalCode())) {
            adderr=true;
        }

        // Validate gender
        if (StringUtils.isBlank(patient.getPerson().getGender())) {
            gerr=true;
        }

        boolean result = !ferr && !lerr && !doberr && !adderr && !gerr;
        if (result) {
            mPatient = patient;
            return true;
        }
        else {
            mEditPatientView.setErrorsVisibility(ferr, lerr, doberr, adderr, gerr);
            return false;
        }
    }

    @Override
    public void editPatient() {
        new PatientApi().updatePatient(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mEditPatientView.finishEditActivity();
            }

            @Override
            public void onErrorResponse() {
                mEditPatientView.setProgressBarVisibility(false);
            }
        });
    }

    @Override
    public String getPatientId() {
        return mID;
    }
}
