package org.openmrs.mobile.dao;

import org.openmrs.mobile.databases.tables.ProviderTable;
import org.openmrs.mobile.models.Provider;

import rx.Observable;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

public class ProviderDAO {
    public Observable<Long> saveProvider(Provider provider) {
        return createObservableIO(() -> new ProviderTable().insert(provider));
    }
}
