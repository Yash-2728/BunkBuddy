package com.tejasdev.bunkbuddy.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.activities.OnboardingActivity
import com.tejasdev.bunkbuddy.databinding.FragmentSignupBinding
import com.tejasdev.bunkbuddy.datamodel.User

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewmodel
    private lateinit var sharedPref: SharedPreferences
    private var enterBtnState = MutableLiveData(true)

    private val PICK_IMAGE_REQUEST = 1
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var userImageUri: Uri = Uri.parse("")


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
        sharedPref = requireActivity().getSharedPreferences(AuthActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        binding.logInTv.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.skipBtn.root.setOnClickListener {
            viewModel.markLoginSkipped()
            nextActivity()
        }
        binding.enterBtn.setOnClickListener {
            if(viewModel.hasInternetConnection()){
                if(enterBtnState.value!!) {
                    val name = binding.usernameTextEdit.text.toString()
                    val email = binding.emailTextEdit.text.toString()
                    val password = binding.passwordTextEdit.text.toString()
                    val vpassword = binding.vPasswordTextEdit.text.toString()
                    if(checkCredentials(name, email, password, vpassword)){
                        showProgressBar()
                        if(userImageUri == Uri.parse("")){
                            SignupUser(name, email, password, "")
                        }
                        else{
                            uploadImage {
                                SignupUser(name, email, password, it.toString())
                            }
                        }
                    }
                }
            }
            else{
                showSnackbar("Internet unavailable")
            }
        }
        binding.editImageIv.setOnClickListener {
            openGallery()
        }
    }

    private fun showProgressBar(){
        enterBtnState.postValue(false)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnText.text = ""
    }
    private fun hideProgressBar(){
        enterBtnState.postValue(true)
        binding.progressBar.visibility = View.GONE
        binding.btnText.text = "Enter"
    }
    private fun SignupUser(
        name: String,
        email: String,
        password: String,
        image: String,
    ){
        viewModel.signupUser(email, name, password, image){ user, message ->
            if(user==null){
                hideProgressBar()
                showSnackbar(message?:"Unknown Error")
            }
            else{
                createSession(user)
                viewModel.markLoginNotSkipped()
                nextActivity()
            }
        }
    }

    private fun uploadImage(callback: (Uri)->Unit){
        val imageRef = storageRef.child("/images/${userImageUri.lastPathSegment}")
        imageRef.putFile(userImageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener {
                        callback(it)
                    }
                    .addOnFailureListener {
                        hideProgressBar()
                        showSnackbar("Something went wrong")
                    }

            }
            .addOnFailureListener {
                hideProgressBar()
                Log.w("image-upload", "$it")
                showSnackbar("Couldn't upload image")
            }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            userImageUri = data.data!!
            binding.userIv.setImageURI(userImageUri)
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

    private fun moveToOnboardingActivity(){
        val intent = Intent(requireActivity(), OnboardingActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
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