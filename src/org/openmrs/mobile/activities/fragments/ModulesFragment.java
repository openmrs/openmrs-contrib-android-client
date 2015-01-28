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
