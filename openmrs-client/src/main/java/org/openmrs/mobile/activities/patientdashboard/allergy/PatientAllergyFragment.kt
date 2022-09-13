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
package org.openmrs.mobile.activities.patientdashboard.allergy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.Allergy
import com.openmrs.android_sdk.library.models.OperationType.PatientAllergyFetching
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionError
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionLocalSuccess
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.addeditallergy.AddEditAllergyActivity
import org.openmrs.mobile.activities.dialog.CustomDialogModel
import org.openmrs.mobile.activities.dialog.CustomPickerDialog
import org.openmrs.mobile.activities.dialog.CustomPickerDialog.onInputSelected
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientAllergyRecyclerViewAdapter.OnLongPressListener
import org.openmrs.mobile.databinding.FragmentPatientAllergyBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce
import java.util.ArrayList

@AndroidEntryPoint
class PatientAllergyFragment : BaseFragment(), OnLongPressListener, onInputSelected {
    private var _binding: FragmentPatientAllergyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientDashboardAllergyViewModel by viewModels()

    private var selectedAllergy: Allergy? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientAllergyBinding.inflate(inflater, container, false)

        setupAdapter()
        setupObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchPatientAllergies()
    }

    private fun setupAdapter() {
        with(binding.recyclerViewAllergy) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = PatientAllergyRecyclerViewAdapter(context, emptyList(), this@PatientAllergyFragment)
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    when (result.operationType) {
                        PatientAllergyFetching -> showAllergyList(result.data)
                        else -> {
                        }
                    }
                }
                is Result.Error -> {
                    when (result.operationType) {
                        PatientAllergyFetching -> ToastUtil.error(getString(R.string.unable_to_fetch_allergies))
                        else -> {
                        }
                    }
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchPatientAllergies() {
        viewModel.fetchAllergies()
    }

    private fun deleteAllergy() {
        viewModel.deleteAllergy(selectedAllergy!!.uuid!!).observeOnce(viewLifecycleOwner, Observer { resultType ->
            when (resultType) {
                AllergyDeletionSuccess -> {
                    ToastUtil.success(getString(R.string.delete_allergy_success))
                    fetchPatientAllergies()
                }
                AllergyDeletionLocalSuccess -> {
                    ToastUtil.notify(getString(R.string.delete_allergy_offline))
                    fetchPatientAllergies()
                }
                AllergyDeletionError -> ToastUtil.error(getString(R.string.delete_allergy_failure))
                else -> {
                }
            }
        })
    }

    private fun showAllergyList(allergies: List<Allergy>) {
        with(binding) {
            progressBar.makeGone()
            if (allergies.isEmpty()) {
                recyclerViewAllergy.makeGone()
                emptyAllergyList.makeVisible()
            } else {
                (recyclerViewAllergy.adapter as PatientAllergyRecyclerViewAdapter).updateList(allergies)
                recyclerViewAllergy.makeVisible()
                emptyAllergyList.makeGone()
            }
        }
    }

    override fun showDialogueBox(allergy: Allergy) {
        selectedAllergy = allergy
        val dialogList = ArrayList<CustomDialogModel>().apply {
            add(CustomDialogModel(getString(R.string.update_allergy_dialog), R.drawable.ic_allergy_edit))
            add(CustomDialogModel(getString(R.string.delete_allergy_dialog), R.drawable.ic_photo_delete))
        }
        CustomPickerDialog(dialogList)
                .apply { setTargetFragment(this@PatientAllergyFragment, 1000) }
                .show(parentFragmentManager, "tag")
    }

    override fun performFunction(position: Int) {
        if (position == 1) {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                    .apply {
                        setTitle(getString(R.string.delete_allergy_title, selectedAllergy!!.allergen!!.codedAllergen!!.display))
                        setMessage(R.string.delete_allergy_description)
                        setCancelable(false)
                        setPositiveButton(R.string.mark_patient_deceased_proceed) { dialog, id ->
                            dialog.cancel()
                            deleteAllergy()
                        }
                        setNegativeButton(R.string.dialog_button_cancel) { dialog, id -> dialog.cancel() }
                    }
                    .create()
                    .show()
        } else {
            Intent(activity, AddEditAllergyActivity::class.java).apply {
                putExtra(PATIENT_ID_BUNDLE, viewModel.getPatient().id.toString())
                putExtra(ALLERGY_UUID, selectedAllergy!!.uuid)
                startActivity(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(patientId: String): PatientAllergyFragment {
            val fragment = PatientAllergyFragment()
            fragment.arguments = bundleOf(Pair(PATIENT_ID_BUNDLE, patientId))
            return fragment
        }
    }
}
