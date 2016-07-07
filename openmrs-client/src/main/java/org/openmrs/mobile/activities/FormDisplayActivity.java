
/*Circular Viewpager indicator code obtained from:
http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html*/

package org.openmrs.mobile.activities;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import org.openmrs.mobile.api.Notifier;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Form;
import org.openmrs.mobile.models.retrofit.Obscreate;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.models.retrofit.Page;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Resource;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FormService;
import org.openmrs.mobile.utilities.InputField;

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
    Notifier notifier=new Notifier();
    List<Observation> observations=new ArrayList<>();
    final RestApi apiService =
            RestServiceBuilder.createService(RestApi.class);
    private long mPatientID;
    private String encountertype;
    private Patient patient;
    private Visit visit;
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
            visit=new VisitDAO().getPatientCurrentVisit(mPatientID);
            encountertype=(String)b.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
            getSupportActionBar().setTitle(formname + " Form");
        }

        btnNext = (Button) findViewById(R.id.btn_next);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);


        FormService formService=new FormService(this);
        form=formService.getFormByName(formname);
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

    void createObs(final Encounter encounter)
    {
        List<Fragment> activefrag=getActiveFragments();
        for (Fragment f:activefrag)
        {
            FormPageFragment formPageFragment=(FormPageFragment)f;
            if(!formPageFragment.checkfields())
                break;
            List<InputField> pageinputlist=formPageFragment.getInputFields();
            inputlist.addAll(pageinputlist);
        }

        for (InputField input:inputlist)
        {
            Obscreate obscreate=new Obscreate();
            obscreate.setConcept(input.getConcept());
            obscreate.setValue(input.getValue());
            LocalDateTime localDateTime = new LocalDateTime();
            obscreate.setObsDatetime(localDateTime.toString());
            obscreate.setPerson(patient.getUuid());
            obscreate.setEncounter(encounter.getUuid());

            Call<Observation> call = apiService.createObs(obscreate);
            call.enqueue(new Callback<Observation>() {
                @Override
                public void onResponse(Call<Observation> call, Response<Observation> response) {
                    if (response.isSuccessful()) {
                        Observation obs = response.body();
                        observations.add(obs);
                        if(observations.size()==inputlist.size())
                            linkvisit(encounter);
                    } else {
                        notifier.notify("Unsuccessful");
                    }
                }

                @Override
                public void onFailure(Call<Observation> call, Throwable t) {
                    notifier.notify(t.getMessage());

                }
            });

        }
    }

    void createEncounter()
    {
        Encounter encounter=new Encounter();
        Patient newPatient=new Patient();
        newPatient.setUuid(patient.getUuid());
        encounter.setPatient(newPatient);
        Visit newVisit=new Visit();
        newVisit.setUuid(visit.getUuid());
        encounter.setVisit(newVisit);
        Resource enctype=new Resource();
        enctype.setUuid(encountertype);
        encounter.setEncounterTypeResource(enctype);

        Call<Encounter> call = apiService.createEncounter(encounter);
        call.enqueue(new Callback<Encounter>() {
            @Override
            public void onResponse(Call<Encounter> call, Response<Encounter> response) {
                if (response.isSuccessful()) {
                    Encounter encounter = response.body();
                    createObs(encounter);

                } else {
                    notifier.notify("Could not save encounter");
                    notifier.notify(response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<Encounter> call, Throwable t) {
                notifier.notify(t.getLocalizedMessage());

            }
        });
    }

    void linkvisit(Encounter encounter)
    {
        encounter.setEncounterType(Encounter.EncounterType.getType(formname));
        List<Encounter> encounterList=visit.getEncounters();
        encounterList.add(encounter);
        new VisitDAO().updateVisit(visit,visit.getId(),mPatientID);
        notifier.notify(formname+" data saved successfully");
        finish();
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
