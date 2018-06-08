package org.openmrs.mobile.dao;

import net.sqlcipher.Cursor;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.databases.tables.ProviderTable;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

/**
 * Created by Chathuranga on 23/05/2018.
 */

public class ProviderDAO {
    public Observable<Long> saveProvider(Provider provider) {
        return createObservableIO(() -> new ProviderTable().insert(provider));
    }
}
