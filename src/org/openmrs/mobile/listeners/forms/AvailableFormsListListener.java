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

package org.openmrs.mobile.listeners.forms;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.odk.collect.android.logic.FormDetails;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.FormsManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.helpers.FormsHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class AvailableFormsListListener extends GeneralErrorListener implements Response.Listener<String> {
    private static final String VITALS_FORM_NAME = "Vitals XForm";
    private static final String FORM_KEY = "form";
    private static final String URL_KEY = "url";
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final FormsManager mFormsManagerCaller;
    protected ACBaseActivity mCallerAdapter;

    public AvailableFormsListListener(FormsManager managerCaller) {
        /*
        It needs manager because it USES request called from it -
        see mFormsManagerCaller.downloadForm
        */
        mFormsManagerCaller = managerCaller;
    }

    public AvailableFormsListListener(FormsManager managerCaller, ACBaseActivity callerAdapter) {
        mFormsManagerCaller = managerCaller;
        mCallerAdapter = callerAdapter;
    }

    @Override
    public void onResponse(String response) {
        mLogger.d(response);
        Document xFormsDoc = writeResponseToDoc(response);
        List<FormDetails> formList = getFormDetails(xFormsDoc);
        if (!formList.isEmpty()) {
            for (FormDetails fd: formList) {
                if (VITALS_FORM_NAME.equals(fd.formName)) {
                    mFormsManagerCaller.downloadForm(
                        FormsHelper.createDownloadFormListener(fd.downloadUrl, fd.formName));
                }
            }
        }
        dismissDialog(false);
    }

    private void dismissDialog(boolean mErrorOccurred) {
        if (mCallerAdapter != null) {
            mCallerAdapter.dismissProgressDialog(mErrorOccurred,
                    R.string.settings_forms_downloaded_succesfull,
                    R.string.settings_forms_downloaded_error);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dismissDialog(true);
        super.onErrorResponse(error);
    }

    private Document writeResponseToDoc(String response) {
        Document xFormsDoc = null;
        try {
            xFormsDoc = new Document();
            KXmlParser parser = new KXmlParser();
            parser.setInput(new StringReader(response));
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    true);
            xFormsDoc.parse(parser);
        } catch (IOException e) {
            mLogger.d(e.toString());
        } catch (XmlPullParserException e) {
            mLogger.d(e.toString());
        }
        return xFormsDoc;
    }

    private List<FormDetails> getFormDetails(Document xFormsDoc) {
        List<FormDetails> formList = new ArrayList<FormDetails>();
        int nElements = xFormsDoc.getChildCount();
        for (int i = 0; i < nElements; ++i) {
            if (xFormsDoc.getType(i) != Element.ELEMENT) {
                // e.g., whitespace (text)
                continue;
            }
            Element xFormElement = xFormsDoc.getElement(i);

            String formId = null;
            String formName = null;
            String formUrl = null;
            int fieldCount = xFormElement.getChildCount();
            for (int j = 0; j < fieldCount; ++j) {
                if (xFormElement.getType(j) != Element.ELEMENT) {
                    // whitespace
                    continue;
                }
                Element child = xFormElement.getElement(j);
                if (FORM_KEY.equals(child.getName())) {
                    formName = child.getText(0);
                    if (null != formName && 0 == formName.length()) {
                        formName = null;
                    }
                }
                // zero value cause url is the only attribute
                if (URL_KEY.equals(child.getAttributeName(0))) {
                    formUrl = child.getAttributeValue(0);
                    formUrl = formUrl.substring(formUrl.lastIndexOf('/') + 1);
                    if (0 == formUrl.length()) {
                        formUrl = null;
                    } else {
                        formId = formUrl.substring(formUrl.lastIndexOf('=') + 1);
                    }
                }

                formList.add(new FormDetails(formName, formUrl, null, formId, null));
            }
        }
        return formList;
    }
}
