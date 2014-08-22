package org.openmrs.client.models;


import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Person {
    private OpenMRSLogger mOpenMRSLogger = OpenMRS.getInstance().getOpenMRSLogger();

    private String gender;
    private String uuid;
    private Date birthDate;
    private Date deathDate;
    private String causeOfDeath;
    private String givenName;
    private String middleName;
    private String familyName;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        if (birthDate != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.birthDate = df.parse(birthDate);
            } catch (ParseException e) {
                mOpenMRSLogger.e("Error during parse birthDate", e);
            }
        }
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        if (deathDate != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.deathDate = df.parse(deathDate);
            } catch (ParseException e) {
                mOpenMRSLogger.e("Error during parse deathDate", e);
            }
        }
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    public String toString() {
        return givenName + " " + familyName;
    }

    public void personMapper(JSONObject personJSON) {
        try {
            uuid = personJSON.getString("uuid");
            gender = personJSON.getString("gender");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (!personJSON.getString("birthdate").equals("null")) {
                    this.birthDate = df.parse(personJSON.getString("birthdate"));
                }
                if (!personJSON.getString("deathDate").equals("null")) {
                    this.deathDate = df.parse(personJSON.getString("deathDate"));
                }
            } catch (ParseException e) {
                mOpenMRSLogger.e("Error during parse date", e);
            }

            causeOfDeath = personJSON.getString("causeOfDeath");

            JSONObject preferredNameJSON = personJSON.getJSONObject("preferredName");
            givenName = preferredNameJSON.getString("givenName");
            middleName = preferredNameJSON.getString("middleName");
            familyName = preferredNameJSON.getString("familyName");
        } catch (JSONException e) {
            mOpenMRSLogger.d(e.toString());
        }
    }
}
