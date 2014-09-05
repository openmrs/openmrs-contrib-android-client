package org.openmrs.client.dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.models.Visit;

public class VisitDAO {

    public void saveVisit(Visit visit, long patientID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        visit.setPatientID(patientID);
        helper.insertVisit(db, visit);
    }
}
