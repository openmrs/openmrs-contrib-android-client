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

package org.openmrs.mobile.bundle;

import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;

import java.io.Serializable;

public class CustomDialogBundle implements Serializable {

    private CustomFragmentDialog.OnClickAction leftButtonAction;
    private CustomFragmentDialog.OnClickAction rightButtonAction;
    private String textViewMessage;
    private String titleViewMessage;
    private String editTextViewMessage;
    private String leftButtonText;
    private String rightButtonText;
    private String progressViewMessage;
    private boolean loadingBar;
    private boolean progressDialog;

    public CustomDialogBundle() {

    }

    public boolean hasProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(boolean progressDialog) {
        this.progressDialog = progressDialog;
    }


    public boolean hasLoadingBar() {
        return loadingBar;
    }

    public void setLoadingBar(boolean loadingBar) {
        this.loadingBar = loadingBar;
    }

    public CustomFragmentDialog.OnClickAction getLeftButtonAction() {
        return leftButtonAction;
    }

    public void setLeftButtonAction(CustomFragmentDialog.OnClickAction leftButtonAction) {
        this.leftButtonAction = leftButtonAction;
    }

    public CustomFragmentDialog.OnClickAction getRightButtonAction() {
        return rightButtonAction;
    }

    public void setRightButtonAction(CustomFragmentDialog.OnClickAction rightButtonAction) {
        this.rightButtonAction = rightButtonAction;
    }

    public String getTextViewMessage() {
        return textViewMessage;
    }

    public void setTextViewMessage(String textViewMessage) {
        this.textViewMessage = textViewMessage;
    }

    public String getLeftButtonText() {
        return leftButtonText;
    }

    public void setLeftButtonText(String leftButtonText) {
        this.leftButtonText = leftButtonText;
    }

    public String getRightButtonText() {
        return rightButtonText;
    }

    public void setRightButtonText(String rightButtonText) {
        this.rightButtonText = rightButtonText;
    }

    public String getTitleViewMessage() {
        return titleViewMessage;
    }

    public void setProgressViewMessage(String progressViewMessage) {
        this.progressViewMessage = progressViewMessage;
    }

    public String getProgressViewMessage() {
        return progressViewMessage;
    }

    public void setTitleViewMessage(String titleViewMessage) {
        this.titleViewMessage = titleViewMessage;
    }

    public String getEditTextViewMessage() {
        return editTextViewMessage;
    }

    public void setEditTextViewMessage(String editTextViewMessage) {
        this.editTextViewMessage = editTextViewMessage;
    }
}
