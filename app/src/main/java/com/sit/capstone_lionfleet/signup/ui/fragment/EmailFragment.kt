package com.sit.capstone_lionfleet.signup.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.signup.viewmodel.EmailViewModel

class EmailFragment : Fragment() {

    companion object {
        fun newInstance() = EmailFragment()
    }

    private lateinit var viewModel: EmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EmailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}