package com.tejasdev.bunkbuddy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {
    private var _binding: ActivityAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewmodel

    private var popupWindow: PopupWindow? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = AuthViewmodel(application, this)

        binding.changePasswordCard.setOnClickListener {

        }

    }

    private fun showChangePasswordPopup(){
        val popupView = LayoutInflater.from(this).inflate(R.layout.change_password_popup, null)

        val p1 = popupView.findViewById<TextInputEditText>(R.id.new_password_edtxt)
        val p2 = popupView.findViewById<TextInputEditText>(R.id.new_password_edtxt2)
        val curr = popupView.findViewById<TextInputEditText>(R.id.curr_password_edtxt)

        val cancelBtn = popupView.findViewById<MaterialCardView>(R.id.cancel_button)
        val saveBtn = popupView.findViewById<MaterialCardView>(R.id.save_btn)

        saveBtn.setOnClickListener {
            if(check(it, p1.text.toString(), p2.text.toString(), curr.text.toString())){
                changePassword(it, p1.text.toString())
            }
        }
        cancelBtn.setOnClickListener {
            popupWindow?.dismiss()
        }
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.isFocusable = true
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun check(view: View, p1: String, p2: String, curr: String): Boolean{
        if(curr!=viewModel.getPassword()) {
            showSnackbar(view, "Wrong password")
            return false
        }
        if(p1!=p2) {
            showSnackbar(view, "Passwords don't match")
            return false
        }
        if(p1.length<8) {
            showSnackbar(view,"Password length should be more than or eqaul to 8")
            return false
        }
        return true
    }
    private fun showSnackbar(view: View, message: String){
        Snackbar.make(view, message, 300).show()
    }
    private fun changePassword(view: View, pass: String) {
        viewModel.changePassword(viewModel.getEmail(), viewModel.getPassword(), pass){ user, message ->
            if(user==null){
                showSnackbar(view, "Password update failed")
            }
            else{
                viewModel.createSession(user)
                popupWindow?.dismiss()
                Toast.makeText(this, "Password, changed successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

}