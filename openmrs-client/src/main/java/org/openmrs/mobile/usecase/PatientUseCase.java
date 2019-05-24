package org.openmrs.mobile.usecase;

import android.util.Log;

import org.openmrs.mobile.api.retrofit.PatientRepository;
import org.openmrs.mobile.api.retrofit.VisitRepository;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.DownloadPatientCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientUseCase {

    public interface DomainCallback<T> {

        void onResult(List<T> results);

        void onFailure(Throwable t);

    }

    private final PatientRepository mPatientRepository;
    private final VisitRepository mVisitsRepository;

    public PatientUseCase() {
        this.mPatientRepository = new PatientRepository();
        this.mVisitsRepository = new VisitRepository();
    }

    public Observable<Patient> findPatientById(String id) {
        return mPatientRepository.findPatientById(id);
    }

    public Observable<Visit> findActiveVisits(Long id) {
        return mVisitsRepository.getActiveVisitByPatientId(id);
    }

    public void syncVisitsData(Patient patient, DomainCallback visitsCallback) {
        mVisitsRepository.syncVisitsData(
                patient, getLastVisitsListener(patient.getId(), visitsCallback));
    }

    public String getVisitIndicator(Visit visit) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                DateUtils.formatString(
                    DateUtils.convertTime(visit.getStartDatetime()),
                    DateUtils.FORMAT_DATE_OF_VISIT));
        return builder.toString();
    }

    @Deprecated
    public void syncPatient(String uuid, Long id, DomainCallback patientCallback, DomainCallback visitsCallback) {
        mPatientRepository.downloadPatientByUuid(uuid, new DownloadPatientCallbackListener() {

            @Override
            public void onPatientDownloaded(Patient patient) {
                patient.setId(id);
                mPatientRepository.savePatient(patient)
                        .subscribe(getPatientObserver());
                mVisitsRepository.syncVisitsData(
                        patient, getLastVisitsListener(id, visitsCallback));
                patientCallback.onResult(Collections.singletonList(patient));
                // TODO 20/05/2019 - in others places new patient data linked to sync visit and vitals calls
            }

            @Override
            public void onPatientPhotoDownloaded(Patient patient) {
                Log.e(ApplicationConstants.TAG, "onPatientPhotoDownloaded");
            }

            @Override
            public void onResponse() {
                // no op
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                patientCallback.onFailure(new Throwable(errorMessage));
            }
        });
    }

    private Observer<Long> getPatientObserver() {
        return new Observer<Long>() {
            @Override
            public void onCompleted() {
                // no op
            }

            @Override
            public void onError(Throwable e) {
                Log.e(ApplicationConstants.TAG, "Failed to save patient", e);
            }

            @Override
            public void onNext(Long aLong) {
                // no op
            }
        };
    }

    private DefaultResponseCallbackListener getLastVisitsListener(Long patientId, DomainCallback callback) {
        return new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                getLastVisit(patientId, callback);
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                callback.onFailure(new Throwable(errorMessage));
            }
        };
    }

    private void getLastVisit(Long patientId, DomainCallback callback) {
        mVisitsRepository.getActiveVisitByPatientId(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Visit>() {
                    @Override
                    public void onCompleted() {
                        // no op
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e);
                    }

                    @Override
                    public void onNext(Visit visit) {
                        callback.onResult(Collections.singletonList(visit));
                    }
        });
    }

}
