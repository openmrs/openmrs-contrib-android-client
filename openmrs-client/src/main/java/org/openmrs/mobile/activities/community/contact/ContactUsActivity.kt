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
package org.openmrs.mobile.activities.community.contact

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.openmrs.android_sdk.utilities.ToastUtil
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.databinding.ActvityContactUsBinding

class ContactUsActivity : ACBaseActivity(), ContactUsContract.View {

    var presenter: ContactUsContract.Presenter? = null
    private lateinit var binding: ActvityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActvityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = ContactUsPresenter()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.contact_us)
        }

        binding.emailLayout.setOnClickListener {
            val sendMailIntent = Intent(Intent.ACTION_SENDTO)
            val mailTo = "mailto:" + binding.contactEmailLink.text.toString()
            sendMailIntent.data = Uri.parse(mailTo)
            try {
                startActivity(sendMailIntent)
            } catch (ex: ActivityNotFoundException) {
                ToastUtil.showShortToast(this, ToastUtil.ToastType.ERROR, getString(R.string.no_mailing_client_found))
            }
        }

        binding.forumLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_forum_url)))
            startActivity(intent)
        }

        binding.ircLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_irc_url)))
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //Disable Contact Option in Menu
        val contactItem = menu.findItem(R.id.actionContact)
        contactItem.isVisible = false
        val logOutItem = menu.findItem(R.id.actionLogout)
        logOutItem.isVisible = false
        val locationItem = menu.findItem(R.id.actionLocation)
        locationItem.isVisible = false
        val settingItem = menu.findItem(R.id.actionSettings)
        settingItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}