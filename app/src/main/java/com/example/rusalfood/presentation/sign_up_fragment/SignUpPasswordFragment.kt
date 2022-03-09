package com.example.rusalfood.presentation.sign_up_fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.rusalfood.R
import com.example.rusalfood.databinding.SignUpPasswordFragmentBinding
import com.example.rusalfood.di.appComponent

class SignUpPasswordFragment : Fragment(R.layout.sign_up_password_fragment) {

    private val binding by viewBinding(SignUpPasswordFragmentBinding::bind)
    private val signUpViewModel: SignUpViewModel by viewModels { requireContext().appComponent.signUpViewModelFactory() }
    private lateinit var sharedPref: SharedPreferences

    companion object {
        fun newInstance() = SignUpPasswordFragment()
        const val SIGN_IN_OK_CODE = 200
        //const val SIGN_IN_ERROR_CODE = 401
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTextChangedListeners()
        initClickListeners()
        initObserving()
        initEncryptedSharedPref()
    }

    private fun initTextChangedListeners() {
        binding.signUpPasswordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                signUpViewModel.checkPasswordInput(s)
            }
        })
    }

    private fun initClickListeners() {
        binding.nextButton.setOnClickListener {
            binding.loginProgressBar.visibility = ProgressBar.VISIBLE
            signUpViewModel.signUp(
                sharedPref,
                requireArguments().getString("email").toString(),
                binding.signUpPasswordField.text.toString()
            )
        }
    }


    private fun initObserving() {
        signUpViewModel.isPasswordInputCorrect.observe(viewLifecycleOwner) {
            binding.nextButton.isEnabled = it
        }

        signUpViewModel.signUpResponse.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity, signUpViewModel.signUpResponse.value?.message, Toast.LENGTH_SHORT
            ).show()
        }

        signUpViewModel.signInResponse.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity,
                signUpViewModel.signInResponse.value?.message,
                Toast.LENGTH_SHORT
            )
                .show()
            binding.loginProgressBar.visibility = ProgressBar.GONE
            //println(sharedPref.getString("token", null))
            if(it.code == SIGN_IN_OK_CODE)
                findNavController().navigate(SignUpPasswordFragmentDirections.toMainFragment(true))
        }

    }

    private fun initEncryptedSharedPref() {
        val masterKey = MasterKey.Builder(requireContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sharedPref = EncryptedSharedPreferences.create(
            requireContext(),
            "encrypted_shared_pref",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}