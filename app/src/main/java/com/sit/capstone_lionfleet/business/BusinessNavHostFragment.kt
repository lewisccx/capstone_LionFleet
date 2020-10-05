package com.sit.capstone_lionfleet.business

import android.content.Context
import androidx.annotation.AnimRes
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BusinessNavHostFragment: NavHostFragment() {

    @Inject
    lateinit var fragmentFactory:BusinessFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }

}