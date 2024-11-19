package com.pth.chatapp_android.ui.signin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pth.chatapp_android.R
import com.pth.chatapp_android.activity.MainActivity
import com.pth.chatapp_android.databinding.FragmentSigninBinding
import com.pth.chatapp_android.utils.getFiledText
import com.pth.chatapp_android.utils.gone
import com.pth.chatapp_android.utils.invisible
import com.pth.chatapp_android.utils.isFiledIsEmpty
import com.pth.chatapp_android.utils.isValidEmail
import com.pth.chatapp_android.utils.toast
import com.pth.chatapp_android.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import com.pth.chatapp_android.utils.Resource

@AndroidEntryPoint
class SigninFragment : Fragment(R.layout.fragment_signin) {

    private val viewModel: SigninViewModel by viewModels()
    private lateinit var binding: FragmentSigninBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSigninBinding.bind(view)

        setClickListener()

        setObserver()

    }
    private fun setObserver() {
        viewModel.signin.observe(viewLifecycleOwner){ it ->
            when(it){
                is Resource.Success -> {
                    loading(false)
                    requireContext().toast("Sign In Successfully!")
                    viewModel.clearSignInData()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    loading(false)
                    it.message?.let {
                        requireContext().toast(it)
                    }
                    viewModel.clearSignInData()
                }
                is Resource.Loading -> {
                    loading(true)
                }
                is Resource.Empty -> {

                }

                else -> {}
            }
        }
    }

    private fun setClickListener(){
        binding.tvCreateNewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        binding.btnSignIn.setOnClickListener {
            if (isValidSignInDetails()) {
                viewModel.signinFunc(binding.etEmail.getFiledText(),binding.etPassword.getFiledText())
            }
        }
    }

    private fun isValidSignInDetails(): Boolean {
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
        return true
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignIn.invisible()
            binding.pbSignIn.visible()
        } else {
            binding.btnSignIn.visible()
            binding.pbSignIn.gone()
        }
    }
}