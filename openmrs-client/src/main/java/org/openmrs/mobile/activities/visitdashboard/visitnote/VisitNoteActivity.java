package org.openmrs.mobile.activities.visitdashboard.visitnote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.api.repository.ConceptRepository;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.usecase.ConceptUseCase;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class VisitNoteActivity extends ACBaseActivity implements VisitNoteContract.View {

    private TextView mPatientName;
    private TextView mPatientDateOfBirth;
    private TextView mPatientId;
    private TextView mPatientVisitIndicator;
    private RecyclerView mDiagnosis;
    private EditText mNote;
    private Button mSubmit;
    private AutoCompleteTextView mPatientDiagnosis;

    private VisitNoteContract.Presenter mPresenter;

    public static void start(Context activity, Long patientId) {
        Intent intent = new Intent(activity, VisitNoteActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_note_form);

        Long patientId = getIntent().getExtras().getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        mPresenter = new VisitNotePresenter(this, patientId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPatientName = findViewById(R.id.patientName);
        mPatientDateOfBirth = findViewById(R.id.patientDateOfBirth);
        mPatientId = findViewById(R.id.patientId);
        mPatientVisitIndicator = findViewById(R.id.activeVisitIndicator);
        mDiagnosis = findViewById(R.id.diagnosis);
        mDiagnosis.setLayoutManager(new LinearLayoutManager(this));
        mDiagnosis.setAdapter(new DiagnosisAdapter());
        mDiagnosis.addItemDecoration(
                new DividerItemDecoration(this, ((LinearLayoutManager) mDiagnosis.getLayoutManager()).getOrientation())
        );

        mNote = findViewById(R.id.patientDoctorsNote);
        mPatientDiagnosis = findViewById(R.id.patientDiagnosis);
        mPatientDiagnosis.setAdapter(new ConceptsAdapter(this, new ConceptUseCase(new ConceptRepository())));
        mPatientDiagnosis.setThreshold(2);
        mPatientDiagnosis.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "Position: " + position);
                ConceptsAdapter concepts = (ConceptsAdapter) mPatientDiagnosis.getAdapter();
                Concept concept = concepts.getItem(position);
                mPresenter.onSelectDiagnosis(concept);
            }
        });
        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSubmitClick();
            }
        });

        mPresenter.onStart();
    }

    @Override
    public void onUpdateKnownDiagnosis(Concept concept) {
        mPatientDiagnosis.setText("");
        DiagnosisAdapter adapter = (DiagnosisAdapter) mDiagnosis.getAdapter();
        adapter.addItem(concept);
    }

    @Override
    public void onError(String message) {
        ToastUtil.error(message);
    }

    @Override
    public void showBio(Patient patient) {
        String birthday = DateUtils.formatString(
                DateUtils.convertTime(patient.getPerson().getBirthdate()),
                DateUtils.FORMAT_DATE_OF_BIRTH);
        mPatientDateOfBirth.setText(birthday);
        mPatientId.setText(patient.getIdentifier().getIdentifier());
        mPatientName.setText(patient.getPerson().getName().getNameString());
    }

    @Override
    public void showActiveVisit(String visitIndicator) {
        mPatientVisitIndicator.setText(visitIndicator);
    }

}
