package com.tejasdev.bunkbuddy.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    lateinit var sharedPreferences: SharedPreferences
    lateinit var viewModel: SubjectViewModel
    private lateinit var navController: NavController
    private var isDarkTheme = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = SubjectDatabase.getDatabase(this)
        val repository = SubjectRepository(db)
        viewModel = SubjectViewModel(application, repository)
        sharedPreferences = this.getSharedPreferences("BunkBuddySharedPref", Context.MODE_PRIVATE)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        isDarkTheme = sharedPreferences.getBoolean("dark_mode", true)
        applyTheme()
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }
    private fun applyTheme(){
        if(isDarkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    fun changeTheme(){
        isDarkTheme = !isDarkTheme
        Toast.makeText(this@MainActivity, "$isDarkTheme", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putBoolean("dark_mode", isDarkTheme)
        editor.apply()
        applyTheme()
    }
}