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

package org.openmrs.mobile.activities.addallergy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.databinding.FragmentAllergyInfoBinding;
import org.openmrs.mobile.models.AllergenCreate;
import org.openmrs.mobile.models.AllergyCreate;
import org.openmrs.mobile.models.AllergyReactionCreate;
import org.openmrs.mobile.models.AllergyUuid;
import org.openmrs.mobile.models.ConceptMembers;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.SystemProperty;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_MILD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_OTHER;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_REACTION;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_SEVERE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.SELECT_ALLERGEN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.SELECT_REACTION;

public class AddEditAllergyFragment extends ACBaseFragment<AddEditAllergyContract.Presenter> implements AddEditAllergyContract.View {
    AllergyCreate allergyCreate = new AllergyCreate();
    AlertDialog alertDialog;
    private FragmentAllergyInfoBinding patientAllergyBinding;
    private List<Resource> foodAllergens = new ArrayList<>();
    private List<Resource> drugAllergens = new ArrayList<>();
    private List<Resource> environmentAllergens = new ArrayList<>();
    private List<Resource> reactionList = new ArrayList<>();
    private String mildSeverity;
    private String moderateSeverity;
    private String severeSeverity;

    public static AddEditAllergyFragment newInstance() {
        return new AddEditAllergyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        patientAllergyBinding = FragmentAllergyInfoBinding.inflate(inflater, container, false);
        View root = patientAllergyBinding.getRoot();
        mPresenter.fetchSystemProperties(this);
        initListeners();
        return root;
    }

    private void initListeners() {
        patientAllergyBinding.mildSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(mildSeverity));
            patientAllergyBinding.mildSeverity.setBackgroundResource(R.drawable.chip_white_rectangle);
            patientAllergyBinding.moderateSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
            patientAllergyBinding.severeSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
        });

        patientAllergyBinding.moderateSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(moderateSeverity));
            patientAllergyBinding.mildSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
            patientAllergyBinding.moderateSeverity.setBackgroundResource(R.drawable.chip_white_rectangle);
            patientAllergyBinding.severeSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
        });

        patientAllergyBinding.severeSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(severeSeverity));
            patientAllergyBinding.mildSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
            patientAllergyBinding.moderateSeverity.setBackgroundResource(R.drawable.chip_grey_rectangle);
            patientAllergyBinding.severeSeverity.setBackgroundResource(R.drawable.chip_white_rectangle);
        });

        patientAllergyBinding.allergenDrug.setOnClickListener(view -> {
            setUpAllergenSpinner(drugAllergens, PROPERTY_DRUG);
            selectChip(patientAllergyBinding.allergenDrug);
            unSelectChip(patientAllergyBinding.allergenFood);
            unSelectChip(patientAllergyBinding.allergenOther);
        });

        patientAllergyBinding.allergenFood.setOnClickListener(view -> {
            setUpAllergenSpinner(foodAllergens, PROPERTY_FOOD);
            unSelectChip(patientAllergyBinding.allergenDrug);
            selectChip(patientAllergyBinding.allergenFood);
            unSelectChip(patientAllergyBinding.allergenOther);
        });

        patientAllergyBinding.allergenOther.setOnClickListener(view -> {
            setUpAllergenSpinner(environmentAllergens, PROPERTY_OTHER);
            unSelectChip(patientAllergyBinding.allergenDrug);
            unSelectChip(patientAllergyBinding.allergenFood);
            selectChip(patientAllergyBinding.allergenOther);
        });

        patientAllergyBinding.cancelButton.setOnClickListener(view -> Objects.requireNonNull(getActivity()).finish());

        patientAllergyBinding.submitButton.setOnClickListener(view -> createAllergy());
    }

    private void unSelectChip(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_grey_rectangle);
        textView.setTextColor(getResources().getColor(R.color.dark_grey_8x));
    }

    private void selectChip(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_orange_rectangle);
        textView.setTextColor(getResources().getColor(R.color.allergy_orange));
    }

    private void createAllergy() {
        allergyCreate.setComment(patientAllergyBinding.commentBox.getText().toString());

        if (null == allergyCreate.getAllergen()) {
            ToastUtil.error(getString(R.string.warning_select_allergen));
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getString(R.string.create_allergy_title));
            alertDialogBuilder
                    .setMessage(R.string.create_allergy_description)
                    .setCancelable(false)
                    .setPositiveButton(R.string.mark_patient_deceased_proceed, (dialog, id) -> {
                        dialog.cancel();
                        showLoading(true, false);
                        mPresenter.createAllergy(allergyCreate);
                    })
                    .setNegativeButton(R.string.dialog_button_cancel, (dialog, id) -> alertDialog.cancel());
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void setUpAllergenSpinner(List<Resource> allergens, String allergenType) {
        String[] allergenArray = new String[allergens.size() + 1];
        allergenArray[0] = SELECT_ALLERGEN;
        for (int i = 0; i < allergens.size(); i++) {
            allergenArray[i + 1] = allergens.get(i).getDisplay();
        }
        ArrayAdapter<String> reactionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, allergenArray);
        patientAllergyBinding.allergySpinner.setAdapter(reactionAdapter);
        patientAllergyBinding.allergySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAllergen = patientAllergyBinding.allergySpinner.getSelectedItem().toString();
                if (!selectedAllergen.equals(allergenArray[0])) {
                    String uuid = null;
                    for (int j = 0; j < allergens.size(); j++) {
                        if (allergens.get(j).getDisplay().equals(selectedAllergen)) {
                            uuid = allergens.get(j).getUuid();
                            break;
                        }
                    }
                    AllergenCreate allergenCreate = new AllergenCreate();
                    allergenCreate.setAllergenType(allergenType);
                    allergenCreate.setCodedAllergen(new AllergyUuid(uuid));
                    allergyCreate.setAllergen(allergenCreate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupReactionSpinner() {
        String[] reactionArray = new String[reactionList.size() + 1];
        reactionArray[0] = SELECT_REACTION;
        for (int i = 0; i < reactionList.size(); i++) {
            reactionArray[i + 1] = reactionList.get(i).getDisplay();
        }
        ArrayAdapter<String> reactionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, reactionArray);
        patientAllergyBinding.reactionSpinner.setAdapter(reactionAdapter);
        patientAllergyBinding.reactionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedReaction = patientAllergyBinding.reactionSpinner.getSelectedItem().toString();
                if (!selectedReaction.equals(reactionArray[0])) {
                    String uuid = null;
                    for (int j = 0; j < reactionList.size(); j++) {
                        if (reactionList.get(j).getDisplay().equals(selectedReaction)) {
                            uuid = reactionList.get(j).getUuid();
                            break;
                        }
                    }
                    AllergyReactionCreate allergyReactionCreate = new AllergyReactionCreate();
                    allergyReactionCreate.setReaction(new AllergyUuid(uuid));
                    List<AllergyReactionCreate> createList = new ArrayList<>();
                    createList.add(allergyReactionCreate);
                    allergyCreate.setReactions(createList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void setConceptMembers(ConceptMembers conceptMembers, String reactions) {
        switch (reactions) {
            case PROPERTY_DRUG:
                drugAllergens = conceptMembers.getMembers();
                // By default
                setUpAllergenSpinner(drugAllergens, PROPERTY_DRUG);
                break;
            case PROPERTY_FOOD:
                foodAllergens = conceptMembers.getMembers();
                break;
            case PROPERTY_REACTION:
                reactionList = conceptMembers.getMembers();
                setupReactionSpinner();
                break;
            default:
                environmentAllergens = conceptMembers.getMembers();
                break;
        }
    }

    @Override
    public void setSeverity(SystemProperty systemProperty) {
        if (systemProperty.getDisplay().contains(PROPERTY_MILD)) {
            mildSeverity = systemProperty.getConceptUUID();
        } else if (systemProperty.getDisplay().contains(PROPERTY_SEVERE)) {
            severeSeverity = systemProperty.getConceptUUID();
        } else {
            moderateSeverity = systemProperty.getConceptUUID();
            // By default
            allergyCreate.setSeverity(new AllergyUuid(moderateSeverity));
        }
    }

    @Override
    public void showLoading(boolean loading, boolean exitScreen) {
        if (loading) {
            patientAllergyBinding.transparentScreen.setVisibility(View.VISIBLE);
            patientAllergyBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            patientAllergyBinding.transparentScreen.setVisibility(View.GONE);
            patientAllergyBinding.progressBar.setVisibility(View.GONE);
            if (exitScreen) {
                getActivity().finish();
            }
        }
    }
}
