package org.openmrs.mobile.activities.visitdashboard.visitnote;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.usecase.PatientUseCase;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VisitNotePresenter
        implements VisitNoteContract.Presenter {

    private final PatientUseCase mPatientUseCase;
    private final VisitNoteContract.View mView;

    private Long mPatientId;

    public VisitNotePresenter(VisitNoteContract.View view, Long id) {
        mView = view;
        mPatientId = id;
        mPatientUseCase = new PatientUseCase();
    }

    @Override
    public void onSelectDiagnosis(Concept concept) {
        mView.onUpdateKnownDiagnosis(concept);
    }

    @Override
    public void onStart() {
        mPatientUseCase.findPatientById(String.valueOf(mPatientId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patient -> {
                    mView.showBio(patient);
                    mPatientUseCase.syncVisitsData(patient, visitCallback);
                });
    }

    @Override
    public void onSubmitClick() {
        mView.onError("Can't do it now. Development on submit request is blocked");
    }

    PatientUseCase.DomainCallback<Visit> visitCallback = new PatientUseCase.DomainCallback<Visit>() {
        @Override
        public void onResult(List<Visit> results) {
            Visit visit = results.get(0);
            mView.showActiveVisit(mPatientUseCase.getVisitIndicator(visit));
        }

        @Override
        public void onFailure(Throwable t) {
            mView.onError(t.getMessage());
        }
    };

}
