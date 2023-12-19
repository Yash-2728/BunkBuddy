package com.tejasdev.bunkbuddy.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    lateinit var sharedPreferences: SharedPreferences
    lateinit var viewModel: SubjectViewModel
    private lateinit var navController: NavController
    private var isDarkTheme = true
    private lateinit var gestureDetector:GestureDetector
    private lateinit var authViewModel: AuthViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = SubjectDatabase.getDatabase(this)
        val repository = SubjectRepository(db)
        viewModel = SubjectViewModel(application, repository)
        sharedPreferences = this.getSharedPreferences("BunkBuddySharedPref", Context.MODE_PRIVATE)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        isDarkTheme = sharedPreferences.getBoolean("dark_mode", true)
        binding.themeSwitch.isChecked = isDarkTheme
        applyTheme()
        setContentView(binding.root)
        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        authViewModel = AuthViewmodel(application, this)
        gestureDetector = GestureDetector(this, object: GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if((e1?.x ?: 0f) < (e2.x ?: 0f)){
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                    return true
                }
                else if((e1?.x?:0f) > (e2.x ?:0f)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
        if(authViewModel.isLogin()) {
            binding.authTv.text = "Logout"
            binding.authIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_logout))
        }
        else if(authViewModel.isSkipped()) {
            binding.authTv.text = "Login"
            binding.authIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_login))
        }


        setUpDrawerLayout()
        binding.llForLogout.setOnClickListener {
            logOut(it)
        }
        binding.llForAccount.setOnClickListener {
            navigateToAccountActivity()
        }
        binding.llForAbout.setOnClickListener {
            showAbout()
        }
        binding.llForPrivacy.setOnClickListener {
            openPrivacyPage()
        }
        binding.themeSwitch.setOnCheckedChangeListener { _, _ ->
            changeTheme()
        }
    }

    private fun setUpDrawerLayout(){
        if(authViewModel.isLogin()){
            Glide.with(this).load(authViewModel.getUserImage()).into(binding.userImageIv)
            Log.w("image-upload", authViewModel.getUserImage().toString())
            binding.usernameTv.text = authViewModel.getUserName()
            binding.emailTv.text = authViewModel.getEmail()
        }
        else {
            binding.userImageIv.setImageDrawable(resources.getDrawable(R.drawable.default_profile))
            binding.usernameTv.text = "Guest"
            binding.emailTv.visibility = View.GONE
        }
    }
    private fun openPrivacyPage() {
        val uri = Uri.parse(PRIVACY_POLICY_LINK)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun showAbout() {
        Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
    }
    private fun showPrivacyPolicy(){
        Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAccountActivity() {
        Toast.makeText(this, "Account and Backup", Toast.LENGTH_SHORT).show()
    }

    private fun logOut(view: View) {
        if(authViewModel.isLogin()){
            authViewModel.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
        else if(authViewModel.isSkipped()) {
            authViewModel.markLoginNotSkipped()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
        else{
            showSnackbar("Could't find logged in user", view)
        }
    }

    private fun showSnackbar(message: String, view: View){
        Snackbar.make(view, message, 200).show()
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
        val editor = sharedPreferences.edit()
        editor.putBoolean("dark_mode", isDarkTheme)
        editor.apply()
        applyTheme()
    }
    companion object{
        const val PRIVACY_POLICY_LINK = "https://bunkbuddyprivacypolicy.blogspot.com/2023/12/privacy-policy-for-bunkbuddy.html"
    }
}