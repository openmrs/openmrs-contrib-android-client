package org.openmrs.mobile.databases;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.openmrs.mobile.models.retrofit.Patient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PatientRegistrationCache {

    private static final String PREFS_NAME = "PatientCache";
    Context context;
    Gson gson = new Gson();


    public PatientRegistrationCache(Context context)
    {
        this.context=context;
    }

    public List<Patient> getPatientList() {
        List<Patient> patientlist=new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String plist = prefs.getString("Patientlist", "");
        if (!(plist.equals("")))
        {
            Type type = new TypeToken<List<Patient>>() {
            }.getType();
            patientlist = gson.fromJson(plist, type);
        }
        return patientlist;
    }

    public void setPatientList(List<Patient> patientlist)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String plist = gson.toJson(patientlist);
        editor.putString("Patientlist", plist);
        editor.apply();
    }

    public void addPatient(Patient p)
    {
            List<Patient> patientlist=getPatientList();
            patientlist.add(p);
            setPatientList(patientlist);
    }

    public void deletePatient(Patient p)
    {
            List<Patient> patientlist=getPatientList();
            patientlist.remove(p);
            setPatientList(patientlist);
    }

}
