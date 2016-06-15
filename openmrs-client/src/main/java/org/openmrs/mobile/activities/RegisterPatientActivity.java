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

package org.openmrs.mobile.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openmrs.mobile.R;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.PersonAddress;
import org.openmrs.mobile.models.retrofit.PersonName;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Person;
import org.openmrs.mobile.api.PatientService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterPatientActivity extends ACBaseActivity {

    LocalDate birthdate;
    DateTime bdt;
    EditText edfname;
    EditText edmname;
    EditText edlname;
    EditText eddob;
    EditText edyr;
    EditText edmonth;
    EditText edaddr1;
    EditText edaddr2;
    EditText edcity;
    EditText edstate;
    EditText edcountry;
    EditText edpostal;

    RadioGroup gen;
    ProgressDialog pd;



    TextView fnameerror;
    TextView lnameerror;
    TextView doberror;
    TextView gendererror;
    TextView addrerror;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        edfname = (EditText) findViewById(R.id.firstname);
        edmname = (EditText) findViewById(R.id.middlename);
        edlname = (EditText) findViewById(R.id.surname);
        eddob=(EditText)findViewById(R.id.dob);
        edyr=(EditText)findViewById(R.id.estyr);
        edmonth=(EditText)findViewById(R.id.estmonth);
        edaddr1=(EditText)findViewById(R.id.addr1);
        edaddr2=(EditText)findViewById(R.id.addr2);
        edcity=(EditText)findViewById(R.id.city);
        edstate=(EditText)findViewById(R.id.state);
        edcountry=(EditText)findViewById(R.id.country);
        edpostal=(EditText)findViewById(R.id.postal);

        gen=(RadioGroup)findViewById(R.id.gender);


        fnameerror=(TextView)findViewById(R.id.fnameerror);
        lnameerror=(TextView)findViewById(R.id.lnameerror);
        doberror=(TextView)findViewById(R.id.doberror);
        gendererror=(TextView)findViewById(R.id.gendererror);
        addrerror=(TextView)findViewById(R.id.addrerror);


        gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                gendererror.setVisibility(View.GONE);
            }
        });


        if (eddob != null) {
            eddob.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Calendar currentDate=Calendar.getInstance();
                    int cYear=currentDate.get(Calendar.YEAR);
                    int cMonth=currentDate.get(Calendar.MONTH);
                    int cDay=currentDate.get(Calendar.DAY_OF_MONTH);

                    edmonth.getText().clear();
                    edyr.getText().clear();


                    DatePickerDialog mDatePicker=new DatePickerDialog(RegisterPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            eddob.setText(selectedday+"/"+selectedmonth+"/"+selectedyear);
                            birthdate = new LocalDate (selectedyear, selectedmonth, selectedday);
                            bdt=birthdate.toDateTimeAtStartOfDay().toDateTime();
                        }
                    },cYear, cMonth, cDay);
                    mDatePicker.setTitle("Select Date");
                    mDatePicker.show();  }
            });
        }


        TextWatcher tw=new TextWatcher() {

            public void afterTextChanged(Editable s) {
                eddob.getText().clear();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        edyr.addTextChangedListener(tw);
        edmonth.addTextChangedListener(tw);



    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void confirm(View v)
    {

        fnameerror.setVisibility(View.INVISIBLE);
        lnameerror.setVisibility(View.INVISIBLE);
        doberror.setVisibility(View.GONE);
        gendererror.setVisibility(View.GONE);
        addrerror.setVisibility(View.GONE);

        boolean ferr=true,lerr=true,doberr=true,gerr=true,adderr=true;


        if(isEmpty(edfname))
        {
            fnameerror.setVisibility(View.VISIBLE);
            ferr=false;
        }
        if(isEmpty(edlname))
        {
            lnameerror.setVisibility(View.VISIBLE);
            lerr=false;
        }

        if(isEmpty(eddob) && (isEmpty(edyr) && isEmpty(edmonth)))
        {
            doberror.setVisibility(View.VISIBLE);
            doberr=false;
        }

        if(isEmpty(edaddr1) && isEmpty(edaddr2) && isEmpty(edcity) && isEmpty(edstate)
                && isEmpty(edcountry) && isEmpty(edpostal))
        {
            addrerror.setVisibility(View.VISIBLE);
            adderr=false;
        }


        if (gen.getCheckedRadioButtonId() == -1)
        {
            gendererror.setVisibility(View.VISIBLE);
            gerr=false;
        }

        if(ferr && lerr && doberr && adderr && gerr)
        {
            registerPatient();
        }
        else{
            ScrollView scrollView=(ScrollView)findViewById(R.id.scrollView);
            scrollView.smoothScrollTo(0, scrollView.getPaddingTop());
        }
    }

    void registerPatient() {
        Person person = new Person();

        PersonAddress address = new PersonAddress();
        address.setAddress1(getInput(edaddr1));
        address.setAddress2(getInput(edaddr2));
        address.setCityVillage(getInput(edcity));
        address.setPostalCode(getInput(edpostal));
        address.setCountry(getInput(edcountry));
        address.setStateProvince(getInput(edstate));
        address.setPreferred(true);

        List<PersonAddress> addresses = new ArrayList<>();
        addresses.add(address);
        person.setAddresses(addresses);

        PersonName name = new PersonName();
        name.setFamilyName(getInput(edlname));
        name.setGivenName(getInput(edfname));
        name.setMiddleName(getInput(edmname));

        List<PersonName> names = new ArrayList<>();
        names.add(name);
        person.setNames(names);

        String[] genderChoices = {"M","F"};
        int index = gen.indexOfChild(findViewById(gen.getCheckedRadioButtonId()));
        String gender = genderChoices[index];
        person.setGender(gender);

        if(isEmpty(eddob)) {
            int yeardiff = isEmpty(edyr)? 0 : Integer.parseInt(edyr.getText().toString());
            int mondiff = isEmpty(edmonth)? 0 : Integer.parseInt(edmonth.getText().toString());
            LocalDate now = new LocalDate();
            bdt = now.toDateTimeAtStartOfDay().toDateTime();
            bdt = bdt.minusYears(yeardiff);
            bdt = bdt.minusMonths(mondiff);
            person.setBirthdateEstimated(true);
            person.setBirthdate(bdt.toString());
        } else {
            person.setBirthdate(bdt.toString());
        }

        final Patient patient = new Patient();
        patient.setPerson(person);

        new PatientService().registerPatient(patient);

        RegisterPatientActivity.this.finish();
    }

    private String getInput(EditText e) {
        if(e.getText() == null) {
            return null;
        } else if (isEmpty(e)) {
            return null;
        } else {
            return e.getText().toString();
        }
    }

}