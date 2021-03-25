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
package org.openmrs.mobile.activities.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.openmrs.mobile.R
import org.openmrs.mobile.databinding.ListGalleryOrCameraItemBinding

class CustomPickerDialog(private val list: List<CustomDialogModel>) : DialogFragment() {
    private var mOnInputSelected: onInputSelected? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_my_custom, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_dialog)
        val adapter = CustomDialogAdapter(activity, list)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter

        return view
    }

    private fun performActivity(position: Int) {
        dialog?.dismiss()
        mOnInputSelected?.performFunction(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnInputSelected = targetFragment as onInputSelected?
        } catch (e: ClassCastException) {
            Log.e("error", "onAttach: ClassCastException : ${e.message}")
        }
    }

    interface onInputSelected {
        fun performFunction(position: Int)
    }

    inner class CustomDialogAdapter(
            var context: Context?,
            var modelList: List<CustomDialogModel>
    ) : RecyclerView.Adapter<CustomDialogAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListGalleryOrCameraItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val customDialogModel = modelList[position]
            with(holder.binding) {
                textView.text = customDialogModel.name
                imageView.setImageResource(customDialogModel.id)
                linearLayoutDialog.setOnClickListener { performActivity(position) }
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class ViewHolder(val binding: ListGalleryOrCameraItemBinding) : RecyclerView.ViewHolder(binding.root)
    }
}