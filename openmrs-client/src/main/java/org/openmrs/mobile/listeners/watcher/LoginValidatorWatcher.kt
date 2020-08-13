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
package org.openmrs.mobile.listeners.watcher

import android.graphics.Color
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.AdapterView.OnItemSelectedListener
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.login.LocationArrayAdapter
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.utilities.StringUtils.notEmpty

//Class used to extract view validation logic
class LoginValidatorWatcher(private val mUrl: EditText,
                            private val mUsername: EditText,
                            private val mPassword: EditText,
                            private val mLocation: Spinner,
                            private val mLoginButton: Button) : TextWatcher, OnItemSelectedListener {
    var isUrlChanged = false
    var isLocationErrorOccurred = false

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // This method is intentionally empty
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // This method is intentionally empty
    }

    override fun afterTextChanged(editable: Editable) {
        // If URL text changed
        if (editable.toString().hashCode() == mUrl.text.toString().hashCode()) {
            urlChanged(editable)
        }
        mLoginButton.isEnabled = isAllDataValid
    }

    private fun urlChanged(editable: Editable) {
        if (OpenMRS.getInstance().serverUrl != editable.toString() && notEmpty(editable.toString())) {
            isUrlChanged = true
        } else if (OpenMRS.getInstance().serverUrl == editable.toString()) {
            isUrlChanged = false
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (position >= 0 && id >= 1) {
            (parent.adapter as LocationArrayAdapter).notifyDataSetChanged()
            //Set Text Color to black once option selected
            val currentText = parent.getChildAt(0) as TextView
            currentText.setTextColor(Color.BLACK)
        } else if (position >= 0 && id == 0L) {
            //If spinner is at start option, append a red * to signify requirement
            val currentText = parent.getChildAt(0) as TextView
            currentText.text = Html.fromHtml(view.context.getString(R.string.login_location_select)
                    + view.context.getString(R.string.req_star))
        }
        mLoginButton.isEnabled = isAllDataValid
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {
        // This method is intentionally empty
    }

    private val isAllDataValid: Boolean
        get() {
            val result = validateNotEmpty(mUsername) && validateNotEmpty(mPassword) && !isUrlChanged && validateLocation()
            if (isLocationErrorOccurred && isUrlChanged) {
                mLocation.isEnabled = false
                mLocation.visibility = View.GONE
            } else {
                mLocation.isEnabled = true
                mLocation.visibility = View.VISIBLE
            }
            if (!result && !isLocationErrorOccurred && isUrlChanged) {
                mLocation.isEnabled = false
                mLocation.visibility = View.GONE
            }
            return result
        }

    private fun validateNotEmpty(editText: EditText): Boolean {
        return notEmpty(editText.text.toString())
    }

    /**
     * Get length of mLocation and check whatever it's empty or not.
     *
     * @return True if a location is selected or no location needed for the OpenMRS instance used.
     */
    private fun validateLocation(): Boolean {
        if (mLocation.adapter != null) {
            if (mLocation.adapter.count > 0) {
                return mLocation.selectedItemId != 0L
            }
        }
        return true
    }

    init {
        mUrl.addTextChangedListener(this)
        mUsername.addTextChangedListener(this)
        mPassword.addTextChangedListener(this)
        mLocation.onItemSelectedListener = this
    }
}