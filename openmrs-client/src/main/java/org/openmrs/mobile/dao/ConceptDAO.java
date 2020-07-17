package org.openmrs.mobile.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.tables.ConceptTable;

import java.util.ArrayList;
import java.util.List;

public class ConceptDAO {
    public long saveOrUpdate(ConceptEntity ConceptEntity) {
        ConceptEntity foundConcept = findConceptsByUUID(ConceptEntity.getUuid());
        if (foundConcept != null) {
            updateConcept(foundConcept.getId(), ConceptEntity);
            return foundConcept.getId();
        } else {
            return saveConcept(ConceptEntity);
        }
    }

    public long saveConcept(ConceptEntity ConceptEntity) {
        return new ConceptTable().insert(ConceptEntity);
    }

    public boolean updateConcept(long conceptID, ConceptEntity ConceptEntity) {
        return new ConceptTable().update(conceptID, ConceptEntity) > 0;
    }

    public List<ConceptEntity> findConceptsByName(String name) {
        List<ConceptEntity> result = new ArrayList<>();
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

    public ConceptEntity findConceptsByUUID(String uuid) {
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
        long cnt = DatabaseUtils.queryNumEntries(db, ConceptTable.TABLE_NAME);
        db.close();
        return cnt;
    }

    private ConceptEntity cursorToConcept(Cursor cursor) {
        ConceptEntity ConceptEntity = new ConceptEntity();

        ConceptEntity.setId(cursor.getLong(cursor.getColumnIndex(ConceptTable.Column.ID)));
        ConceptEntity.setDisplay(cursor.getString(cursor.getColumnIndex(ConceptTable.Column.DISPLAY)));
        ConceptEntity.setUuid(cursor.getString(cursor.getColumnIndex(ConceptTable.Column.UUID)));

        return ConceptEntity;
    }
}