package org.openmrs.mobile.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.ConceptTable;
import org.openmrs.mobile.models.Concept;

import java.util.ArrayList;
import java.util.List;

public class ConceptDAO {

    public long saveOrUpdate(Concept concept) {
        Concept foundConcept = findConceptsByUUID(concept.getUuid());
        if (foundConcept != null) {
            updateConcept(foundConcept.getId(), concept);
            return foundConcept.getId();
        }
        else {
            return saveConcept(concept);
        }
    }

    public long saveConcept(Concept concept) {
        return new ConceptTable().insert(concept);
    }

    public boolean updateConcept(long conceptID, Concept concept) {
        return new ConceptTable().update(conceptID, concept) > 0;
    }

    public List<Concept> findConceptsByName(String name) {
        List<Concept> result = new ArrayList<>();
        String where = String.format("%s like ?", ConceptTable.Column.DISPLAY);
        String[] whereArgs = new String[]{name + "%"};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(ConceptTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {

                while (cursor.moveToNext()) {
                    result.add(cursorToConcept(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    public Concept findConceptsByUUID(String uuid) {
        String where = String.format("%s = ?", ConceptTable.Column.UUID);
        String[] whereArgs = new String[]{uuid};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(ConceptTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToConcept(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public long getConceptsCount() {
            SQLiteDatabase db = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper().getReadableDatabase();
            long cnt  = DatabaseUtils.queryNumEntries(db, ConceptTable.TABLE_NAME);
            db.close();
            return cnt;
    }

    private Concept cursorToConcept(Cursor cursor) {
        Concept concept = new Concept();

        concept.setId(cursor.getLong(cursor.getColumnIndex(ConceptTable.Column.ID)));
        concept.setDisplay(cursor.getString(cursor.getColumnIndex(ConceptTable.Column.DISPLAY)));
        concept.setUuid(cursor.getString(cursor.getColumnIndex(ConceptTable.Column.UUID)));

        return concept;
    }

}