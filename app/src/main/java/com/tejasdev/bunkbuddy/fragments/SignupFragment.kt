package com.tejasdev.bunkbuddy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.databinding.FragmentSignupBinding
import com.tejasdev.bunkbuddy.datamodel.User

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as AuthActivity).viewModel
        binding.logInTv.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.skipBtn.root.setOnClickListener {
            moveToMainActivity()
        }
        binding.enterBtn.setOnClickListener {
            val name = binding.usernameTextEdit.text.toString()
            val email = binding.emailTextEdit.text.toString()
            val password = binding.passwordTextEdit.text.toString()
            val vpassword = binding.vPasswordTextEdit.text.toString()
            if(checkCredentials(name, email, password, vpassword)){
                viewModel.signupUser(email, name, password){user, message ->
                    if(user==null){
                        showSnackbar(message?:"Unknown Error")
                    }
                    else{
                        createSession(user)
                        moveToMainActivity()
                    }
                }
            }
        }


    }

    private fun createSession(user: User) {
        viewModel.createSession(user)
    }
    private fun moveToMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
    }

    private fun checkCredentials(name: String, email: String, password: String, vpassword: String): Boolean{
        if(vpassword!=password){
            showSnackbar("Passwords don't match")
            return false
        }
        if(password.length<8) {
            showSnackbar("Password length should be of more than or equal to 8")
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showSnackbar("Email not valid")
            return false
        }
        if(name.isEmpty()){
            showSnackbar("Username cannot be empty")
            return false
        }
        return true
    }
    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 200).show()
    }
}