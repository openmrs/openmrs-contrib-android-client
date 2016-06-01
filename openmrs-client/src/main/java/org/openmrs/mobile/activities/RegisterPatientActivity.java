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

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.models.location.Location;
import org.openmrs.mobile.models.location.Result;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterPatientActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RadioGroup gen=(RadioGroup)findViewById(R.id.gender);
        final TextView e=(TextView)findViewById(R.id.gendererror);


        gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                e.setVisibility(View.GONE);
                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    //tv.setText("Checked:" + checkedRadioButton.getText());
                }
            }
        });

        final EditText dobEditText=(EditText)findViewById(R.id.dob);

        if (dobEditText != null) {
            dobEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Calendar currentDate=Calendar.getInstance();
                    int cYear=currentDate.get(Calendar.YEAR);
                    int cMonth=currentDate.get(Calendar.MONTH);
                    int cDay=currentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker=new DatePickerDialog(RegisterPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            dobEditText.setText(selectedday+"/"+selectedmonth+"/"+selectedyear);
                        }
                    },cYear, cMonth, cDay);
                    mDatePicker.setTitle("Select date");
                    mDatePicker.show();  }
            });
        }


    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void confirm(View v)
    {
        EditText edfname = (EditText) findViewById(R.id.firstname);
        EditText edlname = (EditText) findViewById(R.id.surname);
        EditText eddob=(EditText)findViewById(R.id.dob);
        EditText edyr=(EditText)findViewById(R.id.estyr);
        EditText edmonth=(EditText)findViewById(R.id.estmonth);
        EditText edaddr1=(EditText)findViewById(R.id.addr1);
        EditText edaddr2=(EditText)findViewById(R.id.addr2);
        EditText edcity=(EditText)findViewById(R.id.city);
        EditText edstate=(EditText)findViewById(R.id.state);
        EditText edcountry=(EditText)findViewById(R.id.country);
        EditText edpostal=(EditText)findViewById(R.id.postal);

        RadioGroup gen=(RadioGroup)findViewById(R.id.gender);


        TextView fnameerror=(TextView)findViewById(R.id.fnameerror);
        TextView lnameerror=(TextView)findViewById(R.id.lnameerror);
        TextView doberror=(TextView)findViewById(R.id.doberror);
        TextView gendererror=(TextView)findViewById(R.id.gendererror);
        TextView addrerror=(TextView)findViewById(R.id.addrerror);

        fnameerror.setVisibility(View.INVISIBLE);
        lnameerror.setVisibility(View.INVISIBLE);
        doberror.setVisibility(View.GONE);
        gendererror.setVisibility(View.GONE);
        addrerror.setVisibility(View.GONE);




        if(isEmpty(edfname))
            fnameerror.setVisibility(View.VISIBLE);
        if(isEmpty(edlname))
            lnameerror.setVisibility(View.VISIBLE);

        if(isEmpty(eddob)&& (isEmpty(edyr)||isEmpty(edmonth)))
            doberror.setVisibility(View.VISIBLE);

        if(isEmpty(edaddr1) && isEmpty(edaddr2) && isEmpty(edcity) && isEmpty(edstate)
                && isEmpty(edcountry) && isEmpty(edpostal))
            addrerror.setVisibility(View.VISIBLE);


        if (gen.getCheckedRadioButtonId() == -1)
            gendererror.setVisibility(View.VISIBLE);





        String BASE_URL=mOpenMRS.getServerUrl()+ ApplicationConstants.API.REST_ENDPOINT;

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().header("Authorization", "Basic "+ getAuthParam()).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RestApi apiService =
                retrofit.create(RestApi.class);

        Call<Location> call = apiService.getlocationlist();
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                int statusCode = response.code();
                Location locationList = response.body();
                for (Result result : locationList.getResults()) {
                    Toast.makeText(RegisterPatientActivity.this,result.getDisplay(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                Toast.makeText(RegisterPatientActivity.this,t.toString(),Toast.LENGTH_SHORT).show();

            }

        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
