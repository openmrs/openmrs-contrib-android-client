package org.openmrs.mobile.listeners.retrofit;

import org.openmrs.mobile.models.retrofit.Patient;

public interface DownloadPatientCallbackListener extends DefaultResponseCallbackListener {

    void onPatientDownloaded(Patient patient);

}
