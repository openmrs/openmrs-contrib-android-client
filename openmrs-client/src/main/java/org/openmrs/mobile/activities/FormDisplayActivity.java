
/*Circular Viewpager indicator code obtained from:
http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html*/

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.FormPageFragment;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.PatientService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.Form;
import org.openmrs.mobile.models.retrofit.Obscreate;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.models.retrofit.Page;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Resource;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FormService;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormDisplayActivity extends ACBaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private FormPageAdapter mFormPageAdapter;
    private ViewPager mViewPager;
    private Button btnNext, btnFinish;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private Form form;
    List<Page> pagelist;
    String formname;

    private long mPatientID;
    private String encountertype;
    private String valuereference;
    private Patient patient;
    List<InputField> inputlist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_display);

        Bundle b = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if(b!=null)
        {
            formname =(String) b.get(ApplicationConstants.BundleKeys.FORM_NAME);
            mPatientID =(long) b.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
            patient=new PatientDAO().findPatientByID(Long.toString(mPatientID));
            encountertype=(String)b.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
            valuereference=(String)b.get(ApplicationConstants.BundleKeys.VALUEREFERENCE);
            getSupportActionBar().setTitle(formname + " Form");
        }

        btnNext = (Button) findViewById(R.id.btn_next);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);


        form=FormService.getForm(valuereference);

        pagelist = form.getPages();


        mFormPageAdapter = new FormPageAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFormPageAdapter);
        mViewPager.addOnPageChangeListener(this);
        setPageIndicators();


    }


    List<WeakReference<Fragment>> fragList = new ArrayList<WeakReference<Fragment>>();
    @Override
    public void onAttachFragment (Fragment fragment) {
        fragList.add(new WeakReference(fragment));
    }

    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for(WeakReference<Fragment> ref : fragList) {
            Fragment f = ref.get();
            if(f != null) {
                if(f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                break;

            case R.id.btn_finish:
                createEncounter();
                break;

        }
    }

    void createEncounter()
    {
        Encountercreate encountercreate=new Encountercreate();
        encountercreate.setPatient(patient.getUuid());
        encountercreate.setEncounterType(encountertype);

        List<Obscreate> observations=new ArrayList<>();

        List<Fragment> activefrag=getActiveFragments();
        boolean valid=true;
        for (Fragment f:activefrag)
        {
            FormPageFragment formPageFragment=(FormPageFragment)f;
            if(!formPageFragment.checkfields())
            {
                valid=false;
                break;
            }
            List<InputField> pageinputlist=formPageFragment.getInputFields();
            inputlist.addAll(pageinputlist);
        }

        if(valid)
        {
            for (InputField input:inputlist) {
                if(input.getValue()!=-1.0)
                {
                    Obscreate obscreate = new Obscreate();
                    obscreate.setConcept(input.getConcept());
                    obscreate.setValue(input.getValue());
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(patient.getUuid());
                    observations.add(obscreate);
                }
            }

            encountercreate.setObservations(observations);
            encountercreate.setFormname(formname);
            encountercreate.setPatientId(mPatientID);
            encountercreate.setObslist();

            new EncounterService().addEncounter(encountercreate);
            finish();
        }

    }

    private void setPageIndicators() {

        dotsCount = mFormPageAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));

        if(dotsCount==1)
        {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class FormPageAdapter extends FragmentPagerAdapter {

        public FormPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FormPageFragment.newInstance(position + 1,pagelist.get(position));
        }

        @Override
        public int getCount() {

            return pagelist.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagelist.get(position).getLabel();
        }
    }

}
