package org.openmrs.mobile.activities.formdisplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import org.openmrs.mobile.models.retrofit.Form;
import org.openmrs.mobile.models.retrofit.Page;
import org.openmrs.mobile.utilities.FormService;

import java.util.List;

class FormPageAdapter extends FragmentPagerAdapter {

    private List<Page> mPageList;

    FormPageAdapter(FragmentManager fm, String valueRef) {
        super(fm);
        Form form = FormService.getForm(valueRef);
        mPageList = form.getPages();
    }

    @Override
    public Fragment getItem(int position) {
        FormDisplayPageFragment fragment = FormDisplayPageFragment.newInstance();
        new FormDisplayPagePresenter(fragment, mPageList.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageList.get(position).getLabel();
    }
}