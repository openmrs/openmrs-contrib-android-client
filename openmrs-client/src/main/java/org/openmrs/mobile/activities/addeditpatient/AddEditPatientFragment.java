/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.addeditpatient;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



public class AddEditPatientFragment extends Fragment implements AddEditPatientContract.View {

    private AddEditPatientContract.Presenter mPresenter;

    private LocalDate birthdate;
    private DateTime bdt;

    private EditText edfname;
    private EditText edmname;
    private EditText edlname;
    private EditText eddob;
    private EditText edyr;
    private EditText edmonth;
    private EditText edaddr1;
    private EditText edaddr2;
    private EditText edcity;
    private AutoCompleteTextView edstate;
    private AutoCompleteTextView edcountry;
    private EditText edpostal;

    private RadioGroup gen;
    private ProgressBar progressBar;

    private TextView fnameerror;
    private TextView lnameerror;
    private TextView doberror;
    private TextView gendererror;
    private TextView addrerror;
    private TextView countryerror;

    private Button submitConfirm;

    private String[] countries;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_info, container, false);
        resolveViews(root);
        addSuggestionsToAutoCompleTextView();
        addListeners();
        fillFields(mPresenter.getPatientToUpdate());
        return root;
    }

    @Override
    public void setPresenter(AddEditPatientContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void finishPatientInfoActivity() {
        getActivity().finish();
    }

    @Override
    public void scrollToTop() {
        ScrollView scrollView=(ScrollView)this.getActivity().findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, scrollView.getPaddingTop());
    }

    @Override
    public void setErrorsVisibility(boolean givenNameError,
                                    boolean familyNameError,
                                    boolean dayOfBirthError,
                                    boolean addressError,
                                    boolean countryError,
                                    boolean genderError) {
        if (givenNameError) {
            fnameerror.setVisibility(View.VISIBLE);
        }
        else {
            fnameerror.setVisibility(View.INVISIBLE);
        }

        if (familyNameError) {
            lnameerror.setVisibility(View.VISIBLE);
        }
        else {
            lnameerror.setVisibility(View.INVISIBLE);
        }

        if (dayOfBirthError) {
            doberror.setVisibility(View.VISIBLE);
        }
        else {
            doberror.setVisibility(View.GONE);
        }

        if (addressError) {
            addrerror.setVisibility(View.VISIBLE);
        }
        else {
            addrerror.setVisibility(View.GONE);
        }

        if (countryError) {
            countryerror.setVisibility(View.VISIBLE);
        }
        else {
            countryerror.setVisibility(View.GONE);
        }

        if (genderError) {
            gendererror.setVisibility(View.VISIBLE);
        }
        else {
            gendererror.setVisibility(View.GONE);
        }
    }

    private Person createPerson() {
        Person person = new Person();

        // Add address
        PersonAddress address = new PersonAddress();
        address.setAddress1(ViewUtils.getInput(edaddr1));
        address.setAddress2(ViewUtils.getInput(edaddr2));
        address.setCityVillage(ViewUtils.getInput(edcity));
        address.setPostalCode(ViewUtils.getInput(edpostal));
        address.setCountry(ViewUtils.getInput(edcountry));
        address.setStateProvince(ViewUtils.getInput(edstate));
        address.setPreferred(true);

        List<PersonAddress> addresses = new ArrayList<>();
        addresses.add(address);
        person.setAddresses(addresses);

        // Add names
        PersonName name = new PersonName();
        name.setFamilyName(ViewUtils.getInput(edlname));
        name.setGivenName(ViewUtils.getInput(edfname));
        name.setMiddleName(ViewUtils.getInput(edmname));

        List<PersonName> names = new ArrayList<>();
        names.add(name);
        person.setNames(names);

        // Add gender
        String[] genderChoices = {"M","F"};
        int index = gen.indexOfChild(getActivity().findViewById(gen.getCheckedRadioButtonId()));
        if (index != -1) {
            person.setGender(genderChoices[index]);
        } else {
            person.setGender(null);
        }

        // Add birthdate
        String birthdate = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);
        if(ViewUtils.isEmpty(eddob)) {
            if (!StringUtils.isBlank(ViewUtils.getInput(edyr)) || !StringUtils.isBlank(ViewUtils.getInput(edmonth))) {
                int yeardiff = ViewUtils.isEmpty(edyr)? 0 : Integer.parseInt(edyr.getText().toString());
                int mondiff = ViewUtils.isEmpty(edmonth)? 0 : Integer.parseInt(edmonth.getText().toString());
                LocalDate now = new LocalDate();
                bdt = now.toDateTimeAtStartOfDay().toDateTime();
                bdt = bdt.minusYears(yeardiff);
                bdt = bdt.minusMonths(mondiff);
                person.setBirthdateEstimated(true);
                birthdate = dateTimeFormatter.print(bdt);
            }
        }
        else {
            birthdate = dateTimeFormatter.print(bdt);
        }
        person.setBirthdate(birthdate);

        return person;
    }

    private Patient createPatient() {
        final Patient patient = new Patient();
        patient.setPerson(createPerson());
        patient.setUuid(" ");
        return patient;
    }

    private Patient updatePatient(Patient patient) {
        patient.setPerson(createPerson());
        return patient;
    }

    @Override
    public void hideSoftKeys(){
        View view = this.getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(this.getActivity());
        }
        InputMethodManager inputMethodManager = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setProgressBarVisibility(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSimilarPatientDialog(List<Patient> patients, Patient newPatient){
        setProgressBarVisibility(false);
        CustomDialogBundle similarPatientsDialog = new CustomDialogBundle();
        similarPatientsDialog.setTitleViewMessage(getString(R.string.similar_patients_dialog_title));
        similarPatientsDialog.setRightButtonText(getString(R.string.dialog_button_register_new));
        similarPatientsDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.REGISTER_PATIENT);
        similarPatientsDialog.setLeftButtonText(getString(R.string.dialog_button_cancel));
        similarPatientsDialog.setLeftButtonAction(CustomFragmentDialog.OnClickAction.CANCEL_REGISTERING);
        similarPatientsDialog.setPatientsList(patients);
        similarPatientsDialog.setNewPatient(newPatient);
        ((AddEditPatientActivity) this.getActivity()).createAndShowDialog(similarPatientsDialog, ApplicationConstants.DialogTAG.SIMILAR_PATIENTS_TAG);
    }

    @Override
    public void startPatientDashbordActivity(Patient patient) {
        Intent intent = new Intent(getActivity(), PatientDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getId());
        startActivity(intent);
    }

    @Override
    public void showUpgradeRegistrationModuleInfo() {
        ToastUtil.notifyLong(getResources().getString(R.string.registration_core_info));
    }

    public static AddEditPatientFragment newInstance() {
        return new AddEditPatientFragment();
    }

    private void resolveViews(View v) {
        edfname = (EditText) v.findViewById(R.id.firstname);
        edmname = (EditText) v.findViewById(R.id.middlename);
        edlname = (EditText) v.findViewById(R.id.surname);
        eddob=(EditText)v.findViewById(R.id.dob);
        edyr=(EditText)v.findViewById(R.id.estyr);
        edmonth=(EditText)v.findViewById(R.id.estmonth);
        edaddr1=(EditText)v.findViewById(R.id.addr1);
        edaddr2=(EditText)v.findViewById(R.id.addr2);
        edcity=(EditText)v.findViewById(R.id.city);
        edstate=(AutoCompleteTextView) v.findViewById(R.id.state);
        edcountry=(AutoCompleteTextView) v.findViewById(R.id.country);
        edpostal=(EditText)v.findViewById(R.id.postal);

        gen=(RadioGroup)v.findViewById(R.id.gender);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

        fnameerror=(TextView)v.findViewById(R.id.fnameerror);
        lnameerror=(TextView)v.findViewById(R.id.lnameerror);
        doberror=(TextView)v.findViewById(R.id.doberror);
        gendererror=(TextView)v.findViewById(R.id.gendererror);
        addrerror=(TextView)v.findViewById(R.id.addrerror);
        countryerror=(TextView)v.findViewById(R.id.countryerror);

        submitConfirm = (Button) v.findViewById(R.id.submitConfirm);
    }

    private void fillFields(final Patient patient) {
        if(patient != null && patient.getPerson() != null) {
            //Change to Update Patient Form
            this.getActivity().setTitle("Update Patient");
            submitConfirm.setText("Update Patient");
            submitConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.confirmUpdate(updatePatient(patient));
                }
            });

            Person person = patient.getPerson();
            edfname.setText(person.getName().getGivenName());
            edmname.setText(person.getName().getMiddleName());
            edlname.setText(person.getName().getFamilyName());

            if (StringUtils.notNull(person.getBirthdate()) || StringUtils.notEmpty(person.getBirthdate())) {
                bdt = DateUtils.convertTimeString(person.getBirthdate());
                eddob.setText(DateUtils.convertTime(DateUtils.convertTime(bdt.toString(), DateUtils.OPEN_MRS_REQUEST_FORMAT),
                        DateUtils.DEFAULT_DATE_FORMAT));
            }

            if (("M").equals(person.getGender())) {
                gen.check(R.id.male);
            } else if (("F").equals(person.getGender())) {
                gen.check(R.id.female);
            }

            edaddr1.setText(person.getAddress().getAddress1());
            edaddr2.setText(person.getAddress().getAddress2());
            edcity.setText(person.getAddress().getCityVillage());
            edstate.setText(person.getAddress().getStateProvince());
            edcountry.setText(person.getAddress().getCountry());
            edpostal.setText(person.getAddress().getPostalCode());
        }
    }



    private void addSuggestionsToAutoCompleTextView() {
        countries = getContext().getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, countries);
        edcountry.setAdapter(adapter);

    }
    private void addSuggestionsToCities(){
        String country_name = edcountry.getText().toString() ;
        country_name = country_name.replace("(","");
        country_name = country_name.replace(")","");
        country_name = country_name.replace(" ","");
        country_name = country_name.replace("-","_");
        country_name = country_name.replace(".","");
        country_name = country_name.replace("'","");
        int resourceId = this.getResources().getIdentifier(country_name.toLowerCase(),"array",getContext().getPackageName());
        if ( resourceId != 0 ) {
            String[] states =  getContext().getResources().getStringArray(resourceId);
            ArrayAdapter<String> state_adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, states);
            edstate.setAdapter(state_adapter);
        }
    }

    private void addListeners() {
        gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                gendererror.setVisibility(View.GONE);
            }
        });

        edcountry.setThreshold(2);
        edcountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edcountry.getText().length() >= edcountry.getThreshold()) {
                    edcountry.showDropDown();
                }
                if (Arrays.asList(countries).contains(edcountry.getText().toString())) {
                    edcountry.dismissDropDown();
                }
            }
        });
        edstate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                addSuggestionsToCities();
            }
        });
        if (eddob != null) {
            eddob.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int cYear;
                    int cMonth;
                    int cDay;

                    if (bdt == null) {
                        Calendar currentDate = Calendar.getInstance();
                        cYear = currentDate.get(Calendar.YEAR);
                        cMonth = currentDate.get(Calendar.MONTH);
                        cDay = currentDate.get(Calendar.DAY_OF_MONTH);
                    } else {
                        cYear = bdt.getYear();
                        cMonth = bdt.getMonthOfYear() - 1;
                        cDay = bdt.getDayOfMonth();
                    }

                    edmonth.getText().clear();
                    edyr.getText().clear();


                    DatePickerDialog mDatePicker=new DatePickerDialog(AddEditPatientFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            int adjustedMonth = selectedmonth + 1;
                            eddob.setText(selectedday+"/"+adjustedMonth+"/"+selectedyear);
                            birthdate = new LocalDate(selectedyear, adjustedMonth, selectedday);
                            bdt=birthdate.toDateTimeAtStartOfDay().toDateTime();
                        }
                    }, cYear, cMonth, cDay);
                    mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                    mDatePicker.setTitle("Select Date");
                    mDatePicker.show();  }
            });
        }
        submitConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.confirmRegister(createPatient());
            }
        });

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(eddob, edmonth, edyr);
        edmonth.addTextChangedListener(textWatcher);
        edyr.addTextChangedListener(textWatcher);
    }

}
