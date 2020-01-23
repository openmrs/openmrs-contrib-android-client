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

package org.openmrs.mobile.activities.contactus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;

public class ContactUsFragment extends ACBaseFragment<ContactUsContract.Presenter> implements ContactUsContract.View {
    private MaterialButton sendButton, forumButton, ircButton;
    private TextInputEditText nameTiet, subjectTiet, messageTiet;
    private View root;

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_contact_us, container, false);
        initViewFields();
        String name_str = nameTiet.getText().toString();
        String subject_str = subjectTiet.getText().toString();
        String msg_str = messageTiet.getText().toString();

        sendButton.setOnClickListener(view -> {
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_us_helpdesk)});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, name_str + getString(R.string.contact_us_get_in_touch));
            sendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_us_regarding) + subject_str + "\n" + msg_str);
            sendMail.setType(getString(R.string.contact_email_type));
            startActivity(Intent.createChooser(sendMail, getString(R.string.choose_a_email_client)));
        });

        forumButton.setOnClickListener(view -> {
            Uri forumUri = Uri.parse(getString(R.string.contact_forum_url));
            Intent forumIntent = new Intent(Intent.ACTION_VIEW, forumUri);
            startActivity(forumIntent);
        });

        ircButton.setOnClickListener(view -> {
            Uri ircUri = Uri.parse(getString(R.string.contact_irc_url));
            Intent ircIntent = new Intent(Intent.ACTION_VIEW, ircUri);
            startActivity(ircIntent);
        });
        return root;
    }

    private void initViewFields() {
        sendButton = root.findViewById(R.id.send);
        ircButton = root.findViewById(R.id.irc_btn);
        forumButton = root.findViewById(R.id.forum_btn);

        nameTiet = root.findViewById(R.id.name);
        subjectTiet = root.findViewById(R.id.subject);
        messageTiet = root.findViewById(R.id.message);
    }
}
