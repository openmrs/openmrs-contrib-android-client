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
package org.openmrs.mobile.activities.addeditallergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.openmrs.android_sdk.library.models.AllergyUuid
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_OTHER
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_REACTION
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentAllergyInfoBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce

@AndroidEntryPoint
class AddEditAllergyFragment : BaseFragment() {
    private var _binding: FragmentAllergyInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditAllergyViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllergyInfoBinding.inflate(inflater, container, false)

        closeCommentBox()
        setActionBarTitle()
        setupObservers()

        return binding.root
    }

    private fun closeCommentBox() = binding.root.requestFocus()

    private fun setActionBarTitle() {
        requireActivity().title = if (viewModel.isUpdateAllergy) getString(R.string.update_allergy_title)
        else getString(R.string.new_allergy_title)
    }

    private fun setupObservers() {
        viewModel.result.observe(viewLifecycleOwner, Observer {
            if (it is Result.Loading) showLoading(true)
            else if (it is Result.Success) {
                setupReactionSpinner(viewModel.reactionList.keys.toList())
                initListeners()
                restoreState()
                showLoading(false)
            } else {
                ToastUtil.error(getString(R.string.allergy_concepts_fetch_error))
                showLoading(false)
            }
        })
    }

    private fun restoreState() = with(binding) {
        viewModel.allergenTypeChipId.let { requireActivity().findViewById<TextView>(it)?.performClick() }
        viewModel.allergenListPosition.let { allergySpinner.setSelection(it) }
        viewModel.selectedReactions.keys.forEach { addReactionChip(it) }
        viewModel.allergenSeverityChipId?.let { requireActivity().findViewById<TextView>(it)?.performClick() }
        viewModel.comment?.let { commentBox.setText(it) }

        if (viewModel.isUpdateAllergy) {
            linearLayoutCategory.makeGone()
            allergySpinner.makeGone()
            finalAllergen.makeVisible()
            finalAllergen.text = viewModel.allergyToUpdate?.allergen?.codedAllergen?.display
        }
    }

    private fun initListeners() = with(binding) {
        allergenDrug.setOnClickListener {
            setupAllergenSpinner(viewModel.drugAllergens.keys.toList(), PROPERTY_DRUG)
            selectChip(allergenDrug)
            unSelectChip(allergenFood)
            unSelectChip(allergenOther)
        }
        allergenFood.setOnClickListener {
            setupAllergenSpinner(viewModel.foodAllergens.keys.toList(), PROPERTY_FOOD)
            unSelectChip(allergenDrug)
            selectChip(allergenFood)
            unSelectChip(allergenOther)
        }
        allergenOther.setOnClickListener {
            setupAllergenSpinner(viewModel.environmentAllergens.keys.toList(), PROPERTY_OTHER)
            unSelectChip(allergenDrug)
            unSelectChip(allergenFood)
            selectChip(allergenOther)
        }
        mildSeverity.setOnClickListener {
            viewModel.allergyCreate.severity = AllergyUuid(viewModel.mildSeverity)
            selectSeverity(mildSeverity)
            unSelectSeverity(moderateSeverity)
            unSelectSeverity(severeSeverity)
        }
        moderateSeverity.setOnClickListener {
            viewModel.allergyCreate.severity = AllergyUuid(viewModel.moderateSeverity)
            unSelectSeverity(mildSeverity)
            selectSeverity(moderateSeverity)
            unSelectSeverity(severeSeverity)
        }
        severeSeverity.setOnClickListener {
            viewModel.allergyCreate.severity = AllergyUuid(viewModel.severeSeverity)
            unSelectSeverity(mildSeverity)
            unSelectSeverity(moderateSeverity)
            selectSeverity(severeSeverity)
        }
        commentBox.doAfterTextChanged { viewModel.allergyCreate.comment = it.toString() }
        submitButton.setOnClickListener { showDialogToSubmit() }
        cancelButton.setOnClickListener { requireActivity().finish() }
    }

    private fun setupAllergenSpinner(allergens: List<String>, allergenType: String) = with(binding.allergySpinner) {
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, allergens)
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                viewModel.selectAllergen(selectedItem.toString(), selectedItemPosition, allergenType)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setupReactionSpinner(reactions: List<String>) = with(binding) {
        reactionSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, reactions)
        reactionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                val selectedItem = reactionSpinner.selectedItem.toString()
                if (viewModel.selectedReactions.containsKey(selectedItem)) {
                    ToastUtil.notify(getString(R.string.allergy_already_selected))
                } else {
                    viewModel.addReaction(selectedItem)
                    addReactionChip(selectedItem)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun showDialogToSubmit() {
        if (viewModel.allergyCreate.allergen == null) {
            ToastUtil.error(getString(R.string.warning_select_allergen))
        } else {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle(getString(R.string.create_allergy_title))
                    .setMessage(R.string.create_allergy_description)
                    .setCancelable(false)
                    .setPositiveButton(R.string.mark_patient_deceased_proceed) { dialog, _ ->
                        dialog.cancel()
                        submitAllergy()
                    }
                    .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.cancel() }
                    .show()
        }
    }

    private fun submitAllergy() {
        showLoading(true)
        viewModel.submitAllergy().observeOnce(viewLifecycleOwner, Observer { success ->
            if (success) {
                ToastUtil.success(getString(R.string.success_creating_allergy))
                requireActivity().finish()
            } else {
                ToastUtil.error(getString(R.string.error_creating_allergy))
            }
            showLoading(false)
        })
    }

    private fun showLoading(loading: Boolean) = with(binding) {
        if (loading) {
            transparentScreen.makeVisible()
            progressBar.makeVisible()
        } else {
            transparentScreen.makeGone()
            progressBar.makeGone()
        }
    }

    private fun addReactionChip(reaction: String) {
        if (reaction == SELECT_REACTION) return
        val chip = Chip(context).apply {
            text = reaction
            isCloseIconVisible = true
            isClickable = false
            setOnCloseIconClickListener { selectedChip: View? ->
                binding.chipGroup.removeView(selectedChip)
                viewModel.removeReaction(text.toString())
            }
        }
        binding.chipGroup.addView(chip)
        binding.linearLayoutReaction.makeVisible()
    }

    private fun unSelectChip(textView: TextView) {
        textView.setBackgroundResource(R.drawable.chip_grey_rectangle)
        textView.setTextColor(resources.getColor(R.color.dark_grey_8x))
    }

    private fun selectChip(textView: TextView) {
        viewModel.selectAllergenTypeChip(textView.id)
        textView.setBackgroundResource(R.drawable.chip_orange_rectangle)
        textView.setTextColor(resources.getColor(R.color.allergy_orange))
    }

    private fun unSelectSeverity(textView: TextView) {
        textView.setBackgroundResource(R.drawable.chip_grey_rectangle)
    }

    private fun selectSeverity(textView: TextView) {
        viewModel.selectAllergenSeverityChip(textView.id)
        textView.setBackgroundResource(R.drawable.chip_white_rectangle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(patientId: String, allergyUuid: String?) = AddEditAllergyFragment().apply {
            arguments = bundleOf(PATIENT_ID_BUNDLE to patientId, ALLERGY_UUID to allergyUuid)
        }
    }
}
