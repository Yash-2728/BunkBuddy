package com.tejasdev.bunkbuddy.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.activities.OnboardingActivity
import com.tejasdev.bunkbuddy.databinding.FragmentLoginBinding
import com.tejasdev.bunkbuddy.datamodel.User


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewmodel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as AuthActivity).viewModel
        binding.signUpTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        sharedPref = requireActivity().getSharedPreferences(AuthActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE)

        binding.skipBtn.root.setOnClickListener {
            moveToMainActivity()
        }

        binding.enterBtn.setOnClickListener {
            val email = binding.emailTextEdit.text.toString()
            val password = binding.passwordTextEdit.text.toString()
            if(checkCredentials(email, password)){
                   viewModel.loginUser(email, password){ user, message ->
                       if(user==null) showSnackbar(message?:"Unknown error")
                       else {
                           createSesssion(user)
                           nextActivity()
                       }
                   }
            }
        }
    }
    private fun nextActivity(){
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
        if(isFirstTime){
            val editor = sharedPref.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
            moveToOnboardingActivity()
        }
        else moveToMainActivity()
    }

    private fun moveToOnboardingActivity() {
        val intent = Intent(requireActivity(), OnboardingActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
    }

    private fun createSesssion(user: User){
        viewModel.createSession(user)
    }

    private fun moveToMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
    }

    private fun checkCredentials(email: String, password: String): Boolean{
        if(password.isEmpty()){
            showSnackbar("Password cannot be empty")
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showSnackbar("Email not valid")
            return false
        }
        return true
    }
    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 200).show()
    }
}