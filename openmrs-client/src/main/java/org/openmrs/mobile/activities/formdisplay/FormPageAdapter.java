package org.openmrs.mobile.activities.formdisplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.utilities.FormService;

import java.util.List;

class FormPageAdapter extends FragmentPagerAdapter {

    private List<Page> mPageList;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

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
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public SparseArray<Fragment> getRegisteredFragments() {
        return mRegisteredFragments;
    }
}