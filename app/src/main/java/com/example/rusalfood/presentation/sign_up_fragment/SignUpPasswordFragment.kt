package com.example.rusalfood.presentation.sign_up_fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rusalfood.databinding.SignUpPasswordFragmentBinding
import com.example.rusalfood.di.appComponent

class SignUpPasswordFragment : Fragment() {

    private var _binding: SignUpPasswordFragmentBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModels { requireContext().appComponent.signUpViewModelFactory() }

    companion object {
        fun newInstance() = SignUpPasswordFragment()
        const val AUTH_OK = "Registration and Authentication successful"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignUpPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextChangedListeners()
        setupClickListeners()
        setupObserving()
    }

    private fun setupTextChangedListeners() {
        binding.signUpPasswordField.addTextChangedListener(signUpViewModel.passwordAfterTextChangedListener)
    }


    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            signUpViewModel.signUpAndGetToken(
                requireArguments().getString("email").toString(),
                binding.signUpPasswordField.text.toString()
            )
        }
    }

    private fun setupObserving() {
        signUpViewModel.isPasswordInputCorrect.observe(viewLifecycleOwner) {
            binding.nextButton.isEnabled = it
        }

        signUpViewModel.signUpResponse.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity, signUpViewModel.signUpResponse.value, Toast.LENGTH_SHORT
            ).show()

            if (it.equals(AUTH_OK))
                findNavController()
                    .navigate(SignUpPasswordFragmentDirections.toMainFragment(true))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}