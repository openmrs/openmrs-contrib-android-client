package org.openmrs.mobile.activities.formlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.FormDisplayActivity;
import org.openmrs.mobile.activities.fragments.ACBaseFragment;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public class FormListFragment extends ACBaseFragment implements FormListContract.View {
    private ListView formList;

    private FormListContract.Presenter mPresenter;

    public static FormListFragment newInstance() {

        return new FormListFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_list, container, false);

        formList = (ListView) root.findViewById(R.id.formlist);
        formList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.listItemClicked(position, ((TextView)view).getText().toString());
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(FormListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFormList(String[] forms) {
        formList.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                forms));
    }

    @Override
    public void startFormDisplayActivity(String formName, Long patientId, String valueRefString, String encounterType) {
        Intent intent = new Intent(getContext(), FormDisplayActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME, formName);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId);
        intent.putExtra(ApplicationConstants.BundleKeys.VALUEREFERENCE, valueRefString);
        intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encounterType);
        ToastUtil.notify("Starting encounter");
        startActivity(intent);
    }

}
