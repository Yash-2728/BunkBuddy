package com.tejasdev.bunkbuddy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    val viewModel: AuthViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(viewModel.isLogin() || viewModel.isSkipped()){
            moveToMainActivity()
        }
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        supportActionBar?.hide()
        setupActionBarWithNavController(navController)
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val SHARED_PREFERENCE = "bunkbuddy_shared_preference"
    }
}