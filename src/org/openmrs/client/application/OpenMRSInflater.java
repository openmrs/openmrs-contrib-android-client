package org.openmrs.client.application;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.utilities.FontsUtil;


public class OpenMRSInflater {
    private LayoutInflater mInflater;

    public OpenMRSInflater(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    public ViewGroup addKeyValueStringView(ViewGroup parentLayout, String label, String data) {
        View view = mInflater.inflate(R.layout.key_value_data_row, null, false);
        TextView labelText = (TextView) view.findViewById(R.id.keyValueDataRowTextLabel);
        labelText.setText(label);
        FontsUtil.setFont(labelText, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);

        TextView dataText = (TextView) view.findViewById(R.id.keyValueDataRowTextData);
        dataText.setText(data);
        FontsUtil.setFont(dataText, FontsUtil.OpenFonts.OPEN_SANS_REGULAR);
        parentLayout.addView(view);
        return parentLayout;
    }

    public ViewGroup addSingleStringView(ViewGroup parentLayout, String label) {
        View view = mInflater.inflate(R.layout.single_text_data_row, null, false);
        TextView labelText = (TextView) view.findViewById(R.id.singleTextRowLabelText);
        labelText.setText(label);
        FontsUtil.setFont(labelText, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);
        parentLayout.addView(view);
        return parentLayout;
    }

}
