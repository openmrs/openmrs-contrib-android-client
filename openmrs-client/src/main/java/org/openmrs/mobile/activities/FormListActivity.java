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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.activeandroid.query.Select;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.FormListService;
import org.openmrs.mobile.api.PatientService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.EncounterType;
import org.openmrs.mobile.models.retrofit.FormResource;
import org.openmrs.mobile.models.retrofit.Resource;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListActivity extends ACBaseActivity {

    static String[] FORMS = null;
    long mPatientID;

    List<FormResource> formresourcelist;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_form_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            mPatientID =(long) b.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        formresourcelist=getFormResourceList();

        Iterator<FormResource> iterator=formresourcelist.iterator();

        while (iterator.hasNext())
        {
            FormResource formResource=iterator.next();
            List<FormResource> valueref=formResource.getResourceList();
            String valuerefString=null;
            for(FormResource resource:valueref)
            {
                if(resource.getName().equals("json"))
                    valuerefString=resource.getValueReference();
            }
            if(valuerefString==null) {
                iterator.remove();
            }
        }

        int size=formresourcelist.size();
        FORMS=new String [size];
        for (int i=0;i<size;i++)
        {
            FORMS[i]=formresourcelist.get(i).getName();
        }
        ListView formlist = (ListView) findViewById(R.id.formlist);
        formlist.setAdapter(new ArrayAdapter<String>(
                FormListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, FORMS));
        formlist.setOnItemClickListener(listclick);

    }


    AdapterView.OnItemClickListener listclick= new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
        final int position, long id) {

            List<FormResource> valueref=formresourcelist.get(position).getResourceList();
            String valuerefString=null;
            for(FormResource resource:valueref)
            {
                if(resource.getName().equals("json"))
                    valuerefString=resource.getValueReference();
            }
            EncounterType enctype=getEncounterType(FORMS[position]);
            String encountertype=enctype.getUuid();
            Intent intent=new Intent(FormListActivity.this, FormDisplayActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME,FORMS[position]);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mPatientID);
            intent.putExtra(ApplicationConstants.BundleKeys.VALUEREFERENCE, valuerefString);
            intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encountertype);
            ToastUtil.notify("Starting encounter");
            startActivity(intent);


        }
    };

}
