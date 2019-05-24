package org.openmrs.mobile.activities.visitdashboard.visitnote;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Patient;

public class VisitNoteContract {

    interface View {

        void onUpdateKnownDiagnosis(Concept concept);

        void onError(String message);

        void showBio(Patient mPatient);

        void showActiveVisit(String visitIndicator);
    }

    interface Presenter {

        void onSelectDiagnosis(Concept concept);

        void onStart();

        void onSubmitClick();
    }
}