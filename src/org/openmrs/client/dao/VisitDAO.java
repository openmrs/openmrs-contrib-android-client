package org.openmrs.client.dao;

import android.database.CursorJoiner;

import net.sqlcipher.Cursor;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.PatientTable;
import org.openmrs.client.databases.tables.Table;
import org.openmrs.client.databases.tables.VisitTable;
import org.openmrs.client.models.Visit;
import org.openmrs.client.models.VisitItemDTO;

import java.util.ArrayList;
import java.util.List;

public class VisitDAO {

    public void saveVisit(Visit visit, long patientID) {
        visit.setPatientID(patientID);
        new VisitTable().insert(visit);
    }

    public List<VisitItemDTO> getAllActiveVisits() {
        List<VisitItemDTO> visitItems = new ArrayList<VisitItemDTO>();

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String sort = VisitTable.Column.PATIENT_KEY_ID + " ASC";
        final Cursor visitCursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, null, null, null, null, sort);

        String[] patientCols = {
                Table.MasterColumn.ID, PatientTable.Column.IDENTIFIER, Table.MasterColumn.DISPLAY
        };
        sort = Table.MasterColumn.ID + " ASC";
        final Cursor patientCursor = helper.getReadableDatabase().query(PatientTable.TABLE_NAME, patientCols, null, null, null, null, sort);

        if (null != visitCursor && null != patientCursor) {
            try {
                final CursorJoiner joiner = new CursorJoiner(visitCursor,
                        new String[]{VisitTable.Column.PATIENT_KEY_ID}, patientCursor,
                        new String[]{Table.MasterColumn.ID});
                for (CursorJoiner.Result result : joiner) {
                    switch (result) {
                        case BOTH:
                            int visitId_CI = visitCursor.getColumnIndex(VisitTable.Column.ID);
                            int visitPlace_CI = visitCursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                            int visitType_CI = visitCursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                            int visitStart_CI = visitCursor.getColumnIndex(VisitTable.Column.START_DATE);
                            int patientName_CI = patientCursor.getColumnIndex(Table.MasterColumn.DISPLAY);
                            int patientIdentifier_CI = patientCursor.getColumnIndex(PatientTable.Column.IDENTIFIER);

                            long visitId = visitCursor.getLong(visitId_CI);
                            String visitPlace = visitCursor.getString(visitPlace_CI);
                            String visitType = visitCursor.getString(visitType_CI);
                            long visitStart = visitCursor.getLong(visitStart_CI);
                            String patientIdentifier = patientCursor.getString(patientIdentifier_CI);
                            String patientName = patientCursor.getString(patientName_CI);

                            VisitItemDTO visitItemDTO = new VisitItemDTO(visitId, patientName,
                                    patientIdentifier, visitPlace, visitType, visitStart);
                            visitItems.add(visitItemDTO);
                            break;
                        default:
                            break;
                    }
                }
            } finally {
                visitCursor.close();
                patientCursor.close();
            }
        }
        return visitItems;
    }

    public List<Visit> getActiveVisitForSelectedPatient(final Long patientID) {
        List<Visit> visits = new ArrayList<Visit>();
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String where = String.format("%s = ?", VisitTable.Column.PATIENT_KEY_ID);
        String[] whereArgs = new String[]{patientID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, null);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        Visit visit = new Visit();
                        visit.setId(cursor.getLong(visitID_CI));
                        visit.setVisitType(cursor.getString(visitType_CI));
                        visit.setVisitPlace(cursor.getString(visitPlace_CI));
                        visit.setStartDate(cursor.getLong(visitStart_CI));
                        visit.setStopDate(cursor.getLong(visitStop_CI));
                        visits.add(visit);
                    }
                } finally {
                    cursor.close();
                }
            }
        return visits;
    }

}
