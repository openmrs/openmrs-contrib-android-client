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

package org.openmrs.mobile.activities.addeditallergy;

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

import com.openmrs.android_sdk.library.models.AllergenCreate;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.AllergyReaction;
import com.openmrs.android_sdk.library.models.AllergyReactionCreate;
import com.openmrs.android_sdk.library.models.AllergyUuid;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.material.chip.Chip;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.databinding.FragmentAllergyInfoBinding;

import java.util.ArrayList;
import java.util.List;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_MILD;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_OTHER;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_REACTION;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_SEVERE;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_ALLERGEN;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_REACTION;

public class AddEditAllergyFragment extends ACBaseFragment<AddEditAllergyContract.Presenter> implements AddEditAllergyContract.View {
    AllergyCreate allergyCreate = new AllergyCreate();
    AlertDialog alertDialog;
    private FragmentAllergyInfoBinding binding;
    private List<Resource> foodAllergens = new ArrayList<>();
    private List<Resource> drugAllergens = new ArrayList<>();
    private List<Resource> environmentAllergens = new ArrayList<>();
    private List<Resource> reactionList = new ArrayList<>();
    private List<String> selectedReactions = new ArrayList<>();
    private String mildSeverity;
    private String moderateSeverity;
    private String severeSeverity;
    private Boolean allergyToUpdate = false;

    public static AddEditAllergyFragment newInstance() {
        return new AddEditAllergyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllergyInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mPresenter.fetchSystemProperties(this);
        initListeners();
        return root;
    }

    private void initListeners() {
        binding.mildSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(mildSeverity));
            selectSeverity(binding.mildSeverity);
            unSelectSeverity(binding.moderateSeverity);
            unSelectSeverity(binding.severeSeverity);
        });

        binding.moderateSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(moderateSeverity));
            unSelectSeverity(binding.mildSeverity);
            selectSeverity(binding.moderateSeverity);
            unSelectSeverity(binding.severeSeverity);
        });

        binding.severeSeverity.setOnClickListener(view -> {
            allergyCreate.setSeverity(new AllergyUuid(severeSeverity));
            unSelectSeverity(binding.mildSeverity);
            unSelectSeverity(binding.moderateSeverity);
            selectSeverity(binding.severeSeverity);
        });

        binding.allergenDrug.setOnClickListener(view -> {
            setUpAllergenSpinner(drugAllergens, PROPERTY_DRUG);
            selectChip(binding.allergenDrug);
            unSelectChip(binding.allergenFood);
            unSelectChip(binding.allergenOther);
        });

        binding.allergenFood.setOnClickListener(view -> {
            setUpAllergenSpinner(foodAllergens, PROPERTY_FOOD);
            unSelectChip(binding.allergenDrug);
            selectChip(binding.allergenFood);
            unSelectChip(binding.allergenOther);
        });

        binding.allergenOther.setOnClickListener(view -> {
            setUpAllergenSpinner(environmentAllergens, PROPERTY_OTHER);
            unSelectChip(binding.allergenDrug);
            unSelectChip(binding.allergenFood);
            selectChip(binding.allergenOther);
        });

        binding.cancelButton.setOnClickListener(view -> requireActivity().finish());

        binding.submitButton.setOnClickListener(view -> createAllergy());
    }

    private void setUpAllergenSpinner(List<Resource> allergens, String allergenType) {
        String[] allergenArray = new String[allergens.size() + 1];
        allergenArray[0] = SELECT_ALLERGEN;
        for (int i = 0; i < allergens.size(); i++) {
            allergenArray[i + 1] = allergens.get(i).getDisplay();
        }
        ArrayAdapter<String> reactionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, allergenArray);
        binding.allergySpinner.setAdapter(reactionAdapter);
        binding.allergySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAllergen = binding.allergySpinner.getSelectedItem().toString();
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
        binding.reactionSpinner.setAdapter(reactionAdapter);
        binding.reactionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedReaction = binding.reactionSpinner.getSelectedItem().toString();
                if (!selectedReaction.equals(reactionArray[0])) {
                    if (selectedReactions.contains(selectedReaction)) {
                        ToastUtil.error(getString(R.string.allergy_already_selected));
                    } else {
                        createReactionChip(selectedReaction);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void createAllergy() {
        allergyCreate.setComment(binding.commentBox.getText().toString());
        allergyCreate.setReactions(getSelectedReactionFromChips());

        if (null == allergyCreate.getAllergen()) {
            ToastUtil.error(getString(R.string.warning_select_allergen));
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
            alertDialogBuilder.setTitle(getString(R.string.create_allergy_title));
            alertDialogBuilder
                    .setMessage(R.string.create_allergy_description)
                    .setCancelable(false)
                    .setPositiveButton(R.string.mark_patient_deceased_proceed, (dialog, id) -> {
                        dialog.cancel();
                        showLoading(true, false);
                        if(!allergyToUpdate) {
                            mPresenter.createAllergy(allergyCreate);
                        } else {
                            mPresenter.updateAllergy(allergyCreate);
                        }
                    })
                    .setNegativeButton(R.string.dialog_button_cancel, (dialog, id) -> alertDialog.cancel());
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
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
            binding.transparentScreen.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.transparentScreen.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            if (exitScreen) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void fillAllergyToUpdate(Allergy mAllergy) {
        if (mAllergy != null) {
            allergyToUpdate = true;
            binding.commentBox.setText(mAllergy.getComment());

            unSelectSeverity(binding.mildSeverity);
            unSelectSeverity(binding.moderateSeverity);
            unSelectSeverity(binding.severeSeverity);
            if (mAllergy.getSeverity() != null) {
                allergyCreate.setSeverity(new AllergyUuid(mAllergy.getSeverity().getUuid()));
                if (mAllergy.getSeverity().getDisplay().contains(PROPERTY_MILD)) {
                    selectSeverity(binding.mildSeverity);
                } else if (mAllergy.getSeverity().getDisplay().contains(PROPERTY_SEVERE)) {
                    selectSeverity(binding.severeSeverity);
                } else {
                    selectSeverity(binding.moderateSeverity);
                }
            }

            List<AllergyReaction> reactions = mAllergy.getReactions();
            for (AllergyReaction allergyReaction : reactions) {
                createReactionChip(allergyReaction.getReaction().getDisplay());
            }

            binding.linearLayoutCategory.setVisibility(View.GONE);
            binding.allergySpinner.setVisibility(View.GONE);
            binding.finalAllergen.setVisibility(View.VISIBLE);
            binding.finalAllergen.setText(mAllergy.getAllergen().getCodedAllergen().getDisplay());
            AllergenCreate allergenCreate = new AllergenCreate();
            allergenCreate.setAllergenType(mAllergy.getAllergen().getAllergenType());
            allergenCreate.setCodedAllergen(new AllergyUuid(mAllergy.getAllergen().getCodedAllergen().getUuid()));
            allergyCreate.setAllergen(allergenCreate);
        }
    }

    private void unSelectChip(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_grey_rectangle);
        textView.setTextColor(getResources().getColor(R.color.dark_grey_8x));
    }

    private void selectChip(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_orange_rectangle);
        textView.setTextColor(getResources().getColor(R.color.allergy_orange));
    }

    private void unSelectSeverity(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_grey_rectangle);
    }

    private void selectSeverity(TextView textView) {
        textView.setBackgroundResource(R.drawable.chip_white_rectangle);
    }

    private void createReactionChip(String selectedReaction) {
        selectedReactions.add(selectedReaction);
        Chip chip = new Chip(getContext());
        chip.setText(selectedReaction);
        chip.setCloseIconVisible(true);
        chip.setClickable(false);
        chip.setClickable(false);
        chip.setOnCloseIconClickListener(selected_chip -> {
            binding.chipGroup.removeView(selected_chip);
            selectedReactions.remove(chip.getText().toString());
        });
        binding.chipGroup.addView(chip);
        binding.linearLayoutReaction.setVisibility(View.VISIBLE);
    }

    private List<AllergyReactionCreate> getSelectedReactionFromChips() {
        List<AllergyReactionCreate> allergyReactionList = new ArrayList<>();

        for (int j = 0; j < binding.chipGroup.getChildCount(); j++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(j);
            String uuid = null;
            for (int i = 0; i < reactionList.size(); i++) {
                if (reactionList.get(i).getDisplay().equals(chip.getText().toString())) {
                    uuid = reactionList.get(i).getUuid();
                    break;
                }
            }
            AllergyReactionCreate allergyReactionCreate = new AllergyReactionCreate();
            allergyReactionCreate.setReaction(new AllergyUuid(uuid));
            allergyReactionList.add(allergyReactionCreate);
        }
        return allergyReactionList;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
