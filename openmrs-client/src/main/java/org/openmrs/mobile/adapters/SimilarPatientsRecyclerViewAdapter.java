package org.openmrs.mobile.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.DashboardActivity;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class SimilarPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SimilarPatientsRecyclerViewAdapter.PatientViewHolder>{

    private List<Patient> patientList;
    private Activity mContext;

    public SimilarPatientsRecyclerViewAdapter(Activity mContext, List<Patient> patientList) {
        this.patientList = patientList;
        this.mContext = mContext;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_synced_patients_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        final Patient patient = patientList.get(position);

        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier().getIdentifier());
        }
        if (null != patient.getPerson().getName()) {
            holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
        }
        try{
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        }
        catch (Exception e)
        {
            holder.mBirthDate.setText(" ");
        }

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPatient(patient);
                Intent intent = new Intent(mContext, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.syncedPatientIdentifier);
            mDisplayName = (TextView) itemView.findViewById(R.id.syncedPatientDisplayName);
            mGender = (TextView) itemView.findViewById(R.id.syncedPatientGender);
            mAge = (TextView) itemView.findViewById(R.id.syncedPatientAge);
            mBirthDate = (TextView) itemView.findViewById(R.id.syncedPatientBirthDate);
        }
    }


    private void downloadPatient(Patient patient) {
        ToastUtil.showShortToast(mContext, ToastUtil.ToastType.NOTICE, R.string.download_started);
        long patientId = new PatientDAO().savePatient(patient);
        new VisitsManager().findVisitsByPatientUUID(
                VisitsHelper.createVisitsByPatientUUIDListener(patient.getUuid(), patientId, (ACBaseActivity) mContext));
    }
}
