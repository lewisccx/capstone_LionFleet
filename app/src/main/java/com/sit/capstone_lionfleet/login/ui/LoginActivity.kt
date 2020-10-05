package com.sit.capstone_lionfleet.login.ui

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.continental.cityfleet.utils.textwatcher.SimpleTextWatcher
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.BusinessActivity
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.hideKeyboard
import com.sit.capstone_lionfleet.core.extension.show
import com.sit.capstone_lionfleet.databinding.ActivityLoginBinding
import com.sit.capstone_lionfleet.login.viewmodel.LoginViewModel
import com.sit.capstone_lionfleet.login.viewstate.LoginStateEvent
import com.sit.capstone_lionfleet.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initBtns()
        initEditTexts()
        observeViewModel()
    }

    private fun initViews() {
        binding.loginLoading.loadingView.hide()
    }

    private fun initEditTexts() {
        binding.editPassword.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(input: Editable?) {
                viewModel.onPasswordInputChanged(input.toString())
            }
        })
        binding.editEmail.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(input: Editable?) {
                viewModel.onEmailInputChanged(input.toString())
            }
        })
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this, Observer {
            MainScope().launch { binding.btnLogin.isEnabled = it.loginButtonEnabled }
        })

        viewModel.loginState.observe(this, Observer {

            when (it) {
                is Resource.Success -> {
                    binding.loginLoading.loadingView.hide()
                    Toasty.success(this, it.value.message).show()
                    viewModel.saveAuthToken(it.value.token)

                    this@LoginActivity.startNewActivity(BusinessActivity::class.java)
                }
                is Resource.Failure -> {
                    binding.loginLoading.loadingView.hide()
                    Toasty.error(this, it.errorBody.toString()).show()
                }
                is Resource.Loading -> {
                    binding.loginLoading.loadingView.show()
                }

            }
        })
    }

    private fun initBtns() {
        binding.btnLogin.setOnClickListener {
            binding.root.hideKeyboard()

            viewModel.setStateEvent(LoginStateEvent.LoginEvent)
        }
    }
}


