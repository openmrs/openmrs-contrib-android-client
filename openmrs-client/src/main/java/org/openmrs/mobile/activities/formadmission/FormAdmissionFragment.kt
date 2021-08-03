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
package org.openmrs.mobile.activities.formadmission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.ToastUtil
import com.openmrs.android_sdk.utilities.ToastUtil.showShortToast
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.databinding.FragmentFormAdmissionBinding
import java.text.SimpleDateFormat
import java.util.*

class FormAdmissionFragment : ACBaseFragment<FormAdmissionContract.Presenter>(), FormAdmissionContract.View {
    private var _binding: FragmentFormAdmissionBinding? = null
    private val binding get() = _binding!!
    private var providerUUID: String? = ""
    private var locationUUID: String? = ""
    private var encounterRoleUUID: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFormAdmissionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initFragmentFields()
        mPresenter?.getEncounterRoles(this)
        mPresenter?.getProviders(this)
        mPresenter?.getLocation(OpenmrsAndroid.getServerUrl(), this)
        return root
    }

    private fun initFragmentFields() {
        val currentDate = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy")
        with(binding) {
            admissionDateHeader.text = df.format(currentDate)
            submitButton.setOnClickListener { createEncounter() }
            cancelButton.setOnClickListener { quitFormEntry() }
        }
    }

    private fun createEncounter() {
        if (providerUUID!!.isEmpty() || locationUUID!!.isEmpty() || encounterRoleUUID!!.isEmpty()) {
            showShortToast(requireContext(), ToastUtil.ToastType.ERROR, getString(R.string.admission_fields_required))
        } else {
            mPresenter!!.createEncounter(providerUUID, locationUUID, encounterRoleUUID)
        }
    }

    override fun updateProviderAdapter(providerList: List<Provider?>?) {
        val providers = arrayOfNulls<String>(providerList!!.size)
        for (i in providerList.indices) {
            providers[i] = providerList[i]!!.display
        }
        val adapterAdmittedBy = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, providers)
        binding.admittedBySpinner.adapter = adapterAdmittedBy
        binding.admittedBySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val providerDisplay = binding.admittedBySpinner.selectedItem.toString()
                for (i in providerList.indices) {
                    if (providerDisplay == providerList[i]!!.display) {
                        providerUUID = providerList[i]!!.uuid
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun updateLocationAdapter(locationList: List<LocationEntity?>?) {
        val locations = arrayOfNulls<String>(locationList!!.size)
        for (i in locationList.indices) {
            locations[i] = locationList[i]!!.display
        }
        val adapterAdmittedTo = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, locations)
        binding.admittedToSpinner.adapter = adapterAdmittedTo
        binding.admittedToSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val locationDisplay = binding.admittedToSpinner.selectedItem.toString()
                for (i in locationList.indices) {
                    if (locationDisplay == locationList[i]!!.display) {
                        locationUUID = locationList[i]!!.uuid
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun updateEncounterRoleList(encounterRoleList: List<Resource?>?) {
        val encounterRole = arrayOfNulls<String>(encounterRoleList!!.size)
        for (i in encounterRoleList.indices) {
            encounterRole[i] = encounterRoleList[i]!!.display
        }
        val adapterEncounterRole = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, encounterRole)
        binding.encounterRoleSpinner.adapter = adapterEncounterRole
        binding.encounterRoleSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val encounterRoleDisplay = binding.encounterRoleSpinner.selectedItem.toString()
                for (i in encounterRoleList.indices) {
                    if (encounterRoleDisplay == encounterRoleList[i]!!.display) {
                        encounterRoleUUID = encounterRoleList[i]!!.uuid
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun showToast(error: String?) {
        error(error!!)
    }

    override fun enableSubmitButton(value: Boolean) {
        binding.submitButton.isEnabled = value
    }

    override fun quitFormEntry() {
        requireActivity().finish()
    }

    companion object {
        fun newInstance(): FormAdmissionFragment {
            return FormAdmissionFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}