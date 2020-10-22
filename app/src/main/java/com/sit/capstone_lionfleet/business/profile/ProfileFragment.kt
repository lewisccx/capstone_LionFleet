package com.sit.capstone_lionfleet.business.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.alan.alansdk.button.AlanButton
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.profile.network.model.User
import com.sit.capstone_lionfleet.core.extension.enable
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import com.sit.capstone_lionfleet.core.extension.showIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_profile_no_driverlicense.*
import org.json.JSONException
import org.json.JSONObject

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
        setVisualState()

        observeViewModel()
        performProfileAction()
        initBtns()
    }

    private fun setVisualState() {
        var visualState: JSONObject? = null
        try {
            visualState = JSONObject("{\"fragment\":\"profile\"}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        requireActivity().findViewById<AlanButton>(R.id.alan_button)
            .setVisualState(visualState.toString())
    }

    private fun initBtns() {
        btnSignOut.setOnClickListener {
            viewModel.setProfileStateEvent(ProfileStateEvent.signOut, requireActivity())
        }
    }

    private fun performProfileAction() {
        viewModel.setProfileStateEvent(ProfileStateEvent.getProfile, requireActivity())
    }

    private fun initViews() {
        profileProgressBar.hide()
    }

    private fun observeViewModel() {
        viewModel.profileDataState.observe(viewLifecycleOwner, Observer { profileDataState ->
            when (profileDataState) {
                is Resource.Success -> {
                    profileProgressBar.hide()
                    updateUI(profileDataState.value)
                }
                is Resource.Failure -> {
                    profileProgressBar.hide()
                    displayError(profileDataState.errorBody!!)
                }
                is Resource.Loading -> {
                    profileProgressBar.show()
                }
            }
        })
    }

    private fun displayError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(user: User) {
        txtName.text = "${user.firstName} ${user.lastName}"
        txtEmail.text = user.email
        txtPhoneNumber.text = "${user.countryCode} ${user.phoneNumber}"
        txtAddress.text = "${user.streetNumber} ${user.street}\n${user.postcode} ${user.city}"
        txtValidated.showIf(!user.licenseActivated)
        btnInvalidDriverLicense.show()
        if (user.licenseActivated) {

            txtHeadline.text = resources.getString(R.string.profile_driver_license_valid_headline)
            txtSubline.text = ""
            rightArrowIcon.setBackgroundColor(resources.getColor(R.color.primary_color))
            rightArrowIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_check_circle_white))
            btnInvalidDriverLicense.enable(false)
        } else {
            txtValidated.text = resources.getString(R.string.profile_driver_license_validated)
        }
        Toast.makeText(requireContext(), user.firstName, Toast.LENGTH_SHORT).show()
    }
}