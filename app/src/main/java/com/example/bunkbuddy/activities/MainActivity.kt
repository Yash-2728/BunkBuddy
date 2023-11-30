package com.example.bunkbuddy.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bunkbuddy.R
import com.example.bunkbuddy.UI.SubjectViewModel
import com.example.bunkbuddy.databinding.ActivityMainBinding
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.repository.SubjectRepository
import com.example.bunkbuddy.room.SubjectDatabase

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    lateinit var sharedPreferences: SharedPreferences
    lateinit var viewModel: SubjectViewModel
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = SubjectDatabase.getDatabase(this)
        val repository = SubjectRepository(db)
        viewModel = SubjectViewModel(application, repository)
        sharedPreferences = this.getSharedPreferences("BunkBuddySharedPref", Context.MODE_PRIVATE)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }
}