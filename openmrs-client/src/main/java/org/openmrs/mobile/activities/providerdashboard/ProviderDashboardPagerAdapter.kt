package org.openmrs.mobile.activities.providerdashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class ProviderDashboardPagerAdapter constructor(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItem(i: Int): Fragment = mFragmentList[i]

    override fun getPageTitle(position: Int): CharSequence = mFragmentTitleList[position]


    override fun getCount(): Int = TAB_COUNT

    companion object {
        private const val TAB_COUNT = 2
    }
}
