package org.openmrs.client.bundle;

import android.content.Context;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;

import java.io.Serializable;

public class CustomDialogBundle implements Serializable {

    private Context context;
    private CustomFragmentDialog.OnClickAction leftButtonAction;
    private CustomFragmentDialog.OnClickAction rightButtonAction;
    private String textViewMessage;
    private String titleViewMessage;
    private String editTextViewMessage;
    private String leftButtonText;
    private String rightButtonText;

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

    public CustomDialogBundle() {

    }

    public CustomDialogBundle(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
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
