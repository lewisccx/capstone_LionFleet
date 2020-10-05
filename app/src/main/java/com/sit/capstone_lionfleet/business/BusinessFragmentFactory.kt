package com.sit.capstone_lionfleet.business

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.sit.capstone_lionfleet.business.profile.ProfileFragment
import javax.inject.Inject

class BusinessFragmentFactory
@Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ProfileFragment::class.java.name -> {
                ProfileFragment()
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}