package com.pth.chatapp_android.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pth.chatapp_android.R
import com.pth.chatapp_android.activity.MainActivity
import com.pth.chatapp_android.databinding.FragmentSignupBinding
import com.pth.chatapp_android.utils.Resource
import com.pth.chatapp_android.utils.getFiledText
import com.pth.chatapp_android.utils.gone
import com.pth.chatapp_android.utils.invisible
import com.pth.chatapp_android.utils.isFiledIsEmpty
import com.pth.chatapp_android.utils.isPasswordIsSame
import com.pth.chatapp_android.utils.isValidEmail
import com.pth.chatapp_android.utils.toast
import com.pth.chatapp_android.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment(R.layout.fragment_signup) {

    private val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)

        setClickListener()

        setObserver()

    }
    private fun setObserver() {
        viewModel.signup.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    loading(false)
                    requireContext().toast("Registered Successfully!")
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    loading(false)
                    resource.message?.let {
                        requireContext().toast(it)
                    }
                }
                is Resource.Loading -> {
                    loading(true)
                }

                else -> {}
            }
        }
    }

    private fun setClickListener(){
        binding.tvSignIn.setOnClickListener { findNavController().popBackStack() }

        binding.btnSignUp.setOnClickListener {
            if (isValidSignUpDetails()) {
                signUp()
            }
        }

    }
    private fun signUp() {
        viewModel.signupFunc(
            binding.etName.getFiledText(),
            binding.etEmail.getFiledText(),
            binding.etPassword.getFiledText()
        )
    }


    private fun isValidSignUpDetails(): Boolean {
        if (binding.etName.isFiledIsEmpty()) {
            requireContext().toast("Please Enter Valid Name")
            return false
        }
        if (binding.etEmail.isFiledIsEmpty()) {
            requireContext().toast("Please Enter Email")
            return false
        }
        if (!binding.etEmail.getFiledText().isValidEmail()) {
            requireContext().toast("Please Enter Valid Email")
            return false
        }
        if (binding.etPassword.isFiledIsEmpty()) {
            requireContext().toast("Please Enter Password")
            return false
        }
        if (binding.etConfirmPassword.isFiledIsEmpty()) {
            requireContext().toast("Please Enter Valid Confirm Password")
            return false
        }
        if (!binding.etPassword.getFiledText()
                .isPasswordIsSame(binding.etConfirmPassword.getFiledText())
        ) {
            requireContext().toast("Password is not matches")
            return false
        }
        return true
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignUp.invisible()
            binding.pbSignUp.visible()
        } else {
            binding.btnSignUp.visible()
            binding.pbSignUp.gone()
        }
    }
}