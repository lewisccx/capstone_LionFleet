package com.sit.capstone_lionfleet.business.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import com.sit.capstone_lionfleet.databinding.FragmentProfileBinding
import com.sit.capstone_lionfleet.profile.network.model.User
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_profile.*

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    val TAG = "ProfileFragment"
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        performProfileAction()
    }

    private fun performProfileAction() {
        viewModel.setProfileStateEvent(ProfileStateEvent.getProfile)
    }

    private fun initViews() {
        profileProgressBar.hide()
    }

    private fun observeViewModel(){
        viewModel.profileDataState.observe(viewLifecycleOwner, Observer { profileDataState->
            when(profileDataState){
                is Resource.Success->{
                    profileProgressBar.hide()
                    Log.d(TAG, profileDataState.value.email)
                    updateUI(profileDataState.value)
                }
                is Resource.Failure->{
                    Log.d(TAG, profileDataState.errorBody.toString())
                    profileProgressBar.hide()
                }
                is Resource.Loading->{
                    Log.d(TAG, "loading")
                    profileProgressBar.show()
                }
            }
        })
    }
    private fun displayError(message: String){

    }
    private fun updateUI(user: User){
        txtHeader.text = user.firstName
        Toast.makeText(requireContext(), user.firstName, Toast.LENGTH_SHORT).show()
    }


}