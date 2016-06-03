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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.ServiceGenerator;
import org.openmrs.mobile.models.retrofit.Address;
import org.openmrs.mobile.models.retrofit.GenID;
import org.openmrs.mobile.models.retrofit.Identifier;
import org.openmrs.mobile.models.retrofit.Location;
import org.openmrs.mobile.models.retrofit.Name;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.PatientResponse;
import org.openmrs.mobile.models.retrofit.Patientidentifier;
import org.openmrs.mobile.models.retrofit.Person;
import org.openmrs.mobile.models.retrofit.Result;
import org.openmrs.mobile.net.volley.wrappers.StringRequestDecorator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterPatientActivity extends ACBaseActivity {

    String locUUID;
    String genId;
    ArrayList<String> idtypelist=new ArrayList<>();

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
                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    //tv.setText("Checked:" + checkedRadioButton.getText());
                }
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

        if(isEmpty(eddob)&& (isEmpty(edyr)||isEmpty(edmonth)))
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
            registerpatient();
    }


    void getlocuuid()
    {
        RestApi apiService =
                ServiceGenerator.createService(RestApi.class);
        Call<Location> call = apiService.getlocationlist();
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                Location locationList = response.body();
                for (Result result : locationList.getResults()) {
                    if ((result.getDisplay().trim()).equalsIgnoreCase((mOpenMRS.getLocation().trim()))) {
                        locUUID = result.getUuid();
                        Toast.makeText(RegisterPatientActivity.this,"Location "+ locUUID,Toast.LENGTH_SHORT).show();
                        getgenId();
                    }
                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                Toast.makeText(RegisterPatientActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
                locUUID ="";
            }

        });
    }



    void getgenId()
    {
        String IDGEN_BASE_URL= mOpenMRS.getServerUrl()+'/';
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IDGEN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi apiService =
                retrofit.create(RestApi.class);
        Call<GenID> call = apiService.getidlist(mOpenMRS.getUsername(),mOpenMRS.getPassword());
        call.enqueue(new Callback<GenID>() {
            @Override
            public void onResponse(Call<GenID> call, Response<GenID> response) {
                GenID idList = response.body();
                genId=idList.getIdentifiers().get(0);
                Toast.makeText(RegisterPatientActivity.this,"ID : "+genId,Toast.LENGTH_SHORT).show();
                getidentifiertypeuuid();

            }

            @Override
            public void onFailure(Call<GenID> call, Throwable t) {
                Toast.makeText(RegisterPatientActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

        });
    }


    void getidentifiertypeuuid()
    {
        RestApi apiService =
                ServiceGenerator.createService(RestApi.class);
        Call<Patientidentifier> call = apiService.getidentifiertypelist();
        call.enqueue(new Callback<Patientidentifier>() {
            @Override
            public void onResponse(Call<Patientidentifier> call, Response<Patientidentifier> response) {
                Patientidentifier idresList = response.body();
                for (Result result : idresList.getResults()) {
                        idtypelist.add(result.getUuid());
                        Toast.makeText(RegisterPatientActivity.this,"IDTYPE "+result.getUuid(),Toast.LENGTH_SHORT).show();

                }
                setPOSTpatient();
            }

            @Override
            public void onFailure(Call<Patientidentifier> call, Throwable t) {
                Toast.makeText(RegisterPatientActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

        });
    }


    void registerpatient()
    {
        getlocuuid();
    }

    void setPOSTpatient()
    {

        String firstname=getInput(edfname);
        String middlename=getInput(edmname);
        String lastname=getInput(edlname);

        String genchoices[]={"M","F"};
        int index = gen.indexOfChild(findViewById(gen.getCheckedRadioButtonId()));
        String gender=genchoices[index];

        String address1,address2,city,state,country,postal;
        address1=getInput(edaddr1);
        address2=getInput(edaddr2);
        city=getInput(edcity);
        state=getInput(edstate);
        country=getInput(edcountry);
        postal=getInput(edpostal);

        Address address=new Address();

        if(address1!=null) address.setAddress1(address1);
        if(address2!=null) address.setAddress2(address2);
        if(city!=null) address.setCityVillage(city);
        if(postal!=null) address.setPostalCode(postal);
        if(country!=null) address.setCountry(country);
        if(state!=null) address.setStateProvince(state);
        address.setPreferred(true);

        List<Address> addresses=new ArrayList<>();
        addresses.add(address);

        Name name=new Name();
        name.setFamilyName(lastname);
        name.setGivenName(firstname);
        if(middlename!=null) name.setMiddleName(middlename);

        List<Name> names=new ArrayList<>();
        names.add(name);

        Person person=new Person();

        person.setGender(gender);
        person.setNames(names);
        person.setAddresses(addresses);

        if(isEmpty(eddob))
        {
            int yeardiff= Integer.parseInt(edyr.getText().toString());
            int mondiff=Integer.parseInt(edmonth.getText().toString());
            LocalDate now = new LocalDate();
            bdt=now.toDateTimeAtStartOfDay().toDateTime();
            bdt=bdt.minusYears(yeardiff);
            bdt=bdt.minusMonths(mondiff);
            person.setBirthdateEstimated(bdt.toString());
        }
        else
            person.setBirthdate(bdt.toString());

        List<Identifier> identifiers=new ArrayList<>();

        for (String idtype : idtypelist) {
            Identifier identifier=new Identifier();
            identifier.setIdentifier(genId);
            identifier.setLocation(locUUID);
            identifier.setIdentifierType(idtype);
            identifiers.add(identifier);

        }


        Patient patient=new Patient();
        patient.setPerson(person);
        patient.setIdentifiers(identifiers);


        RestApi apiService =
                ServiceGenerator.createService(RestApi.class);
        Call<PatientResponse> call = apiService.createpatient(patient);
        call.enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                PatientResponse newpatient = response.body();
                Toast.makeText(RegisterPatientActivity.this,"Patient created with UUID "+ newpatient.getUuid()
                        ,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(RegisterPatientActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

        });



    }


    String getInput(EditText e)
    {
        if(e.getText()==null)
            return null;
        else
            return e.getText().toString();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
