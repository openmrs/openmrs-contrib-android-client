package org.openmrs.mobile.activities

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    val isActive: Boolean get() = isAdded
}
