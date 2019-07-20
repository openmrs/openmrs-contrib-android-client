/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.providermanagerdashboard.addprovider;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddProviderFragment extends ACBaseFragment<AddProviderContract.Presenter>
        implements AddProviderContract.View {

    private FloatingActionButton doneFAB;
    private TextInputEditText firstNameEt, lastNameEt, identifierEt;
    private TextInputLayout firstNameTIL, lastNameTIL, identifierTIL;

    private Provider editProvider = null;
    private ArrayList<Provider> existingProviders;

    public static AddProviderFragment newInstance() {
        return new AddProviderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_provider, container, false);

        editProvider = (Provider) (Objects.requireNonNull(getActivity()).getIntent()
                .getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE));

        existingProviders = (ArrayList<Provider>) (Objects.requireNonNull(getActivity()).getIntent()
                .getSerializableExtra(ApplicationConstants.BundleKeys.EXISTING_PROVIDERS_BUNDLE));

        setupUI(root);
        return root;
    }

    void setupUI(View root) {
        doneFAB = root.findViewById(R.id.add_provider_done_fab);
        firstNameEt = root.findViewById(R.id.add_provider_firstname_tiet);
        lastNameEt = root.findViewById(R.id.add_provider_lastname_tiet);
        identifierEt = root.findViewById(R.id.add_provider_identifier_tiet);
        firstNameTIL = root.findViewById(R.id.add_provider_firstname_til);
        lastNameTIL = root.findViewById(R.id.add_provider_lastname_til);
        identifierTIL = root.findViewById(R.id.add_provider_identifier_til);

        if (editProvider != null) {
            String displayName = editProvider.getPerson().getDisplay();
            String firstName, lastName;

            if (displayName == null) {
                firstName = editProvider.getPerson().getName().getGivenName();
                lastName = editProvider.getPerson().getName().getFamilyName();
            } else {
                firstName = displayName.substring(0, displayName.indexOf(' '));
                lastName = displayName.substring(displayName.lastIndexOf(' ') + 1);
            }

            firstNameEt.setText(firstName);
            lastNameEt.setText(lastName);
            identifierEt.setText(editProvider.getIdentifier());
        }

        doneFAB.setOnClickListener(v -> {
            if (validateFields()) {
                String firstName = Objects.requireNonNull(firstNameEt.getText()).toString();
                String lastName = Objects.requireNonNull(lastNameEt.getText()).toString();
                String identifier = Objects.requireNonNull(identifierEt.getText()).toString();

                Person person = mPresenter.createPerson(firstName, lastName);
                Provider provider;

                if (editProvider == null) {
                    provider = mPresenter.createNewProvider(person, identifier);
                    List<Provider> matchingProviders = mPresenter.getMatchingProviders(existingProviders, provider);

                    if (matchingProviders.size() > 0) {
                        showMatchingProvidersDialog(matchingProviders, provider);
                    } else {
                        setProviderResult(provider);
                    }

                } else {
                    provider = mPresenter.editExistingProvider(editProvider, person, identifier);
                    setProviderResult(provider);
                }

            }

        });
    }

    @Override
    public boolean validateFields() {
        String emptyError = getString(R.string.emptyerror);

        // Invalid characters for given name only
        String givenNameError = getString(R.string.fname_invalid_error);
        // Invalid family name
        String familyNameError = getString(R.string.lname_invalid_error);

        // First name validation
        if (ViewUtils.isEmpty(firstNameEt)) {
            firstNameTIL.setErrorEnabled(true);
            firstNameTIL.setError(emptyError);
            return false;
        } else if (!ViewUtils.validateText(ViewUtils.getInput(firstNameEt), ViewUtils.ILLEGAL_CHARACTERS)) {
            firstNameTIL.setErrorEnabled(true);
            firstNameTIL.setError(givenNameError);
            return false;
        } else {
            firstNameTIL.setErrorEnabled(false);
        }


        // Family name validation
        if (ViewUtils.isEmpty(lastNameEt)) {
            lastNameTIL.setErrorEnabled(true);
            lastNameTIL.setError(emptyError);
            return false;
        } else if (!ViewUtils.validateText(ViewUtils.getInput(lastNameEt), ViewUtils.ILLEGAL_CHARACTERS)) {
            lastNameTIL.setErrorEnabled(true);
            lastNameTIL.setError(familyNameError);
            return false;
        } else {
            lastNameTIL.setErrorEnabled(false);
        }

        // identifier validation
        if (ViewUtils.isEmpty(identifierEt)) {
            identifierTIL.setErrorEnabled(true);
            identifierTIL.setError(emptyError);
            return false;
        } else {
            identifierTIL.setErrorEnabled(false);
        }
        return true;
    }

    public void showMatchingProvidersDialog(List<Provider> matchingProviders, Provider provider) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(Objects.requireNonNull(getActivity()).getString(R.string.title_dialog_matching_provider));

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_matching_provider_alert_dialog, null);

        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.custom_matching_provider_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        MatchingProviderRecyclerViewAdapter adapter = new MatchingProviderRecyclerViewAdapter(getActivity(), matchingProviders);
        recyclerView.setAdapter(adapter);

        builder.setPositiveButton(getActivity().getString(R.string.dialog_matching_provider_positive_btn), (dialog, which) -> {
            setProviderResult(provider);

        }).setNegativeButton(getActivity().getString(R.string.dialog_button_cancel), (dialog, which) -> {
            // Do nothing and cancel the dialog box
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * This will set the Intent result with new/existing providers
     * @param provider
     */
    public void setProviderResult(Provider provider) {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider);
        Objects.requireNonNull(getActivity()).setResult(RESULT_OK, intent);

        getActivity().finish();
    }
}
