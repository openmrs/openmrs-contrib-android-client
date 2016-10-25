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

package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.ScreenSlidePagerAdapter;
import org.openmrs.mobile.models.ModuleInfo;

public class ModulesFragment extends ACBaseFragment {

    private static final String POSITION_KEY = "position_key";
    private int mPosition;

    public static final ModulesFragment newInstance(int position) {
        ModulesFragment fragment = new ModulesFragment();
        final Bundle args = new Bundle(1);
        args.putInt(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void showData(ViewGroup viewGroup) {
        final int idMultiplier = 100;
        int i = (mPosition + 1) * idMultiplier;
        RelativeLayout layout = (RelativeLayout) viewGroup.findViewById(R.id.pageLayout);
        if (ScreenSlidePagerAdapter.isEmpty()) {
            return;
        }
        for (ModuleInfo moduleInfo : ScreenSlidePagerAdapter.getPage(mPosition)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(viewGroup.getContext());
            btn.setText(moduleInfo.getName());
            btn.setId(i);
            if (i % idMultiplier % ScreenSlidePagerAdapter.ITEMS_IN_ROW != 0) {
                params.addRule(RelativeLayout.RIGHT_OF, i - 1);
            }
            if (i % idMultiplier >= ScreenSlidePagerAdapter.ITEMS_IN_ROW) {
                params.addRule(RelativeLayout.BELOW, i - ScreenSlidePagerAdapter.ITEMS_IN_ROW);
            }
            i++;
            layout.addView(btn, params);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_modules, container, false);
        mPosition = this.getArguments().getInt(POSITION_KEY, 1);
        showData(rootView);
        return rootView;
    }
}
