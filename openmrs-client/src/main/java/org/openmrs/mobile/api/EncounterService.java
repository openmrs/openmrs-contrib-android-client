package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncounterService extends IntentService {
    OpenMRS openMrs = OpenMRS.getInstance();
    EncounterDAO encounterDAO=new EncounterDAO();
    final RestApi apiService =
            RestServiceBuilder.createService(RestApi.class);
    Long mPatientID;
    Visit visit;
    String formname;

    public EncounterService() {
        super("Save Encounter");
    }

    public void addEncounter(final Encountercreate encountercreate,Long mPatientID
                                ,Visit visit,String formname) {

        //Save encounter here
        this.mPatientID=mPatientID;
        this.visit=visit;
        this.formname=formname;

        syncEncounter(encountercreate);
    }

    public void syncEncounter(final Encountercreate encountercreate) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        Boolean syncstate = prefs.getBoolean("sync", true);

        if (syncstate) {

            Call<Encounter> call = apiService.createEncounter(encountercreate);
            call.enqueue(new Callback<Encounter>() {
                @Override
                public void onResponse(Call<Encounter> call, Response<Encounter> response) {
                    if (response.isSuccessful()) {
                        Encounter encounter = response.body();
                        linkvisit(encounter);
                    } else {
                        ToastUtil.error("Could not save encounter");
                    }
                }

                @Override
                public void onFailure(Call<Encounter> call, Throwable t) {
                    ToastUtil.error(t.getLocalizedMessage());

                }
            });

        } else {
            ToastUtil.error("Sync is off.");
        }

    }


    void linkvisit(Encounter encounter)
    {
        encounter.setEncounterTypeToken(Encounter.EncounterTypeToken.getType(formname));
        List<Encounter> encounterList=visit.getEncounters();
        encounterList.add(encounter);
        new VisitDAO().updateVisit(visit,visit.getId(),mPatientID);
        ToastUtil.success(formname+" data saved successfully");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(isNetworkAvailable()) {
            //code for getting unsaved encountercreate list from db to be added here



        } else {
            ToastUtil.error("No internet connection. Patient Registration data is saved locally " +
                    "and will sync when internet connection is restored. ");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) openMrs.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}