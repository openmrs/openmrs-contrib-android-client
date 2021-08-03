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
package org.openmrs.mobile.activities.formlist

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.openmrs.android_sdk.library.models.FormCreate
import com.openmrs.android_sdk.library.models.FormData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import org.json.JSONException
import org.json.JSONObject
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.formadmission.FormAdmissionActivity
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import org.openmrs.mobile.databinding.FragmentFormListBinding
import com.openmrs.android_sdk.utilities.ApplicationConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.nio.charset.StandardCharsets

public final class FormListFragment : ACBaseFragment<FormListContract.Presenter?>(), FormListContract.View {
    private var _binding: FragmentFormListBinding? = null
    private val binding get() = _binding!!
    private var snackbar: Snackbar? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFormListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.formlist.setOnItemClickListener({ parent: AdapterView<*>?, view: View, position: Int, id: Long -> mPresenter!!.listItemClicked(position, (view as TextView).text.toString()) })
        return root
    }

    override fun showFormList(forms: Array<String?>?) {
        if (forms!!.size == 0) {
            snackbar = Snackbar.make(binding.root, ApplicationConstants.EMPTY_STRING, Snackbar.LENGTH_INDEFINITE)
            val customSnackBarView = layoutInflater.inflate(R.layout.snackbar, null)
            val snackBarLayout = snackbar!!.view as SnackbarLayout
            snackBarLayout.setPadding(0, 0, 0, 0)
            val noticeField = customSnackBarView.findViewById<TextView>(R.id.snackbar_text)
            noticeField.setText(R.string.snackbar_no_forms_found)
            val dismissButton = customSnackBarView.findViewById<TextView>(R.id.snackbar_action_button)
            val typeface = Typeface.createFromAsset(requireActivity().assets, ApplicationConstants.TypeFacePathConstants.ROBOTO_MEDIUM)
            dismissButton.typeface = typeface
            dismissButton.setOnClickListener { v: View? -> snackbar!!.dismiss() }
            snackBarLayout.addView(customSnackBarView, 0)
            snackbar!!.show()
        }
        binding.formlist.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                forms)
    }

    override fun startFormDisplayActivity(formName: String?, patientId: Long?, valueRefString: String?, encounterType: String?) {
        val intent = Intent(context, FormDisplayActivity::class.java)
        intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME, formName)
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId)
        intent.putExtra(ApplicationConstants.BundleKeys.VALUEREFERENCE, valueRefString)
        intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encounterType)
        startActivity(intent)
    }

    override fun startAdmissionFormActivity(formName: String?, patientId: Long?, encounterType: String?) {
        val intent = Intent(context, FormAdmissionActivity::class.java)
        intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME, formName)
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId)
        intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encounterType)
        startActivity(intent)
    }

    override fun setPresenter(presenter: FormListContract.Presenter?) {
        mPresenter = presenter
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun formCreate(uuid: String?, formName: String?): Boolean? {
        formCreateFlag = false
        val apiService = RestServiceBuilder.createService(RestApi::class.java)
        if (formName!!.contains("admission")) {
            val obj = loadJSONFromAsset("admission.json")
            val call2 = apiService.formCreate(uuid, obj)
            call2.enqueue(object : Callback<FormCreate> {
                override fun onResponse(call: Call<FormCreate>, response: Response<FormCreate>) {
                    if (response.isSuccessful && response.body()!!.name == "json") {
                        formCreateFlag = true
                    }
                }

                override fun onFailure(call: Call<FormCreate>, t: Throwable) {
                    //This method is lef blank intentionally
                }
            })
        } else if (formName.contains("vitals")) {
            val obj = loadJSONFromAsset("vitals1.json")
            val obj2 = loadJSONFromAsset("vitals2.json")
            val call2 = apiService.formCreate(uuid, obj)
            call2.enqueue(object : Callback<FormCreate> {
                override fun onResponse(call: Call<FormCreate>, response: Response<FormCreate>) {
                    if (response.isSuccessful && response.body()!!.name == "json") {
                        formCreateFlag = true
                    }
                }

                override fun onFailure(call: Call<FormCreate>, t: Throwable) {
                    //This method is lef blank intentionally
                }
            })
            val call = apiService.formCreate(uuid, obj2)
            call.enqueue(object : Callback<FormCreate> {
                override fun onResponse(call: Call<FormCreate>, response: Response<FormCreate>) {
                    if (response.isSuccessful && response.body()!!.name == "json") {
                        formCreateFlag = true
                    }
                }

                override fun onFailure(call: Call<FormCreate>, t: Throwable) {
                    //This method is lef blank intentionally
                }
            })
        } else if (formName.contains("visit note")) {
            val obj = loadJSONFromAsset("visit_note.json")
            val call2 = apiService.formCreate(uuid, obj)
            call2.enqueue(object : Callback<FormCreate> {
                override fun onResponse(call: Call<FormCreate>, response: Response<FormCreate>) {
                    if (response.isSuccessful && response.body()!!.name == "json") {
                        formCreateFlag = true
                    }
                }

                override fun onFailure(call: Call<FormCreate>, t: Throwable) {
                    //This method is lef blank intentionally
                }
            })
        }
        return formCreateFlag
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun loadJSONFromAsset(filename: String): FormData? {
        var json: String? = null
        json = try {
            val `is` = requireActivity().assets.open("forms/$filename")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        var obj: JSONObject? = null
        try {
            obj = JSONObject(json)
            val data = FormData()
            data.name = obj.getString("name")
            data.dataType = obj.getString("dataType")
            data.valueReference = obj.getString("valueReference")
            return data
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    override fun showError(formName: String?) {
        error(getString(R.string.no_such_form_name_error_message, formName))
    }

    companion object {
        private var formCreateFlag: Boolean? = null
        fun newInstance(): FormListFragment {
            return FormListFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}