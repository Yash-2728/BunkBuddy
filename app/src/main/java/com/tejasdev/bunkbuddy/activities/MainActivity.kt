package com.tejasdev.bunkbuddy.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.alarm.AlarmReceiver
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.util.constants.ALERTS_OFF
import com.tejasdev.bunkbuddy.util.constants.ALERTS_ON
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    lateinit var sharedPreferences: SharedPreferences
    val viewModel: SubjectViewModel by viewModels()
    private lateinit var navController: NavController
    private var isDarkTheme = true
    var isNotificationEnabled = false
    private lateinit var gestureDetector:GestureDetector
    private val authViewModel: AuthViewmodel by viewModels()
    private lateinit var editor: SharedPreferences.Editor
    val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        setUpSharedPref()
        setUpSwitchWithFeatureFlags()
        checkNotificationSettings()
        applyTheme()
        setUpAuthViewModel()
        setUpDrawerLayout()
        binding.llForLogout.setOnClickListener {
            logOut(it)
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

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) scheduleAlarms()
            else removeScheduledAlarms()
        }

        binding.llForHistory.setOnClickListener {
            navController.navigate(R.id.historyFragment)
            closeDrawer()
        }
    }

    private fun setUpAuthViewModel() {

        if(authViewModel.isLogin()) {
            binding.authTv.text = getString(R.string.logout)
            binding.authIcon.setImageDrawable(
                ResourcesCompat.getDrawable(this.resources, R.drawable.ic_logout, null)
            )
        }
        else if(authViewModel.isSkipped()) {
            binding.authTv.text = getString(R.string.log_in)
            binding.authIcon.setImageDrawable(
                ResourcesCompat.getDrawable(this.resources, R.drawable.ic_login, null)
            )
        }
    }

    private fun setUpSwitchWithFeatureFlags() {
        isDarkTheme = sharedPreferences.getBoolean(DARK_MODE_ENABLED, true)
        isNotificationEnabled = sharedPreferences.getBoolean(NOTIFICATION_ENABLED, false)
        binding.themeSwitch.isChecked = isDarkTheme
        binding.notificationSwitch.isChecked = isNotificationEnabled
    }

    private fun setUpSharedPref() {
        sharedPreferences = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }



    private fun removeScheduledAlarms() {
        val lectures = viewModel.getAllLecturesSync()
        changeNotificationSwitchState()
        for(lecture in lectures){
            alarmViewModel.cancelAlarm(lecture)
        }
    }

    private fun changeNotificationSwitchState() {
        isNotificationEnabled = !isNotificationEnabled
        editor.putBoolean(NOTIFICATION_ENABLED, isNotificationEnabled)
        binding.notificationSwitch.isChecked = isNotificationEnabled
        editor.apply()
        val dayAndDate = getDayAndDate()

        val historyItem = HistoryItem(
            if(isNotificationEnabled) ALERTS_ON else ALERTS_OFF,
            if(isNotificationEnabled) this.getString(R.string.alerts_on) else this.getString(R.string.alerts_off),
            time = dayAndDate[0],
            date = dayAndDate[1]
        )
        viewModel.addHistory(historyItem)
    }

    private fun getDayAndDate():List<String> {
        val currentDate = Calendar.getInstance().time

        return listOf(
            SimpleDateFormat("hh:mm a", Locale.US).format(currentDate),
            SimpleDateFormat("d MMM yyyy", Locale.US).format(currentDate)
        )
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                AlarmReceiver.NOTIFICATION_CHANNEL_ID,
                AlarmReceiver.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            val manager = this.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }
    private fun scheduleAlarms() {
        val lectures = viewModel.getAllLecturesSync()
        changeNotificationSwitchState()
        createNotificationChannel()
        for(lecture in lectures){
            val perc = getAttendancePerc(lecture)
            if(perc<lecture.subject.requirement){
                alarmViewModel.setAlarm(lecture)
            }
        }
    }

    private fun getAttendancePerc(lecture: Lecture): Double {
        return ((lecture.subject.attended.toDouble()).div(lecture.subject.attended.toDouble() + lecture.subject.missed.toDouble()))*100
    }

    private fun checkNotificationSettings(){
        if(!isNotificationPermissionGranted(this)){
            requestNotificationPermission(this)
        }
    }

    private fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            activity.startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            binding.notificationSwitch.isClickable = isNotificationPermissionGranted(this)
        }
    }
    private fun isNotificationPermissionGranted(context: Context): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        return notificationManager.areNotificationsEnabled()
    }

    private fun setUpDrawerLayout(){
        if(authViewModel.isLogin()){
            if(authViewModel.hasInternetConnection()){
                if(authViewModel.getUserImage()!=Uri.parse("")) Glide.with(this).load(authViewModel.getUserImage()).into(binding.userImageIv)
            }
            else showSnackbar(binding.userImageIv, getString(R.string.error_loading_image_message))

            binding.usernameTv.text = authViewModel.getUserName()
            binding.emailTv.text = authViewModel.getEmail()
        }
        else {
            binding.userImageIv.setImageDrawable(resources.getDrawable(R.drawable.default_profile))
            binding.usernameTv.text = this.getString(R.string.guest)
            binding.emailTv.visibility = View.GONE
        }

        gestureDetector = GestureDetector(this, object: GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                Log.w("motion-event-main", "${e1?.x} ${e1?.y} ${e2.x} ${e2.y} $velocityX $velocityY")
                if((e1?.x ?: 0f) < e2.x){
                    openDrawer()
                    return true
                }
                else if((e1?.x?:0f) > e2.x){
                    closeDrawer()
                    return true
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }
    private fun openDrawer(){
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    private fun closeDrawer(){
        binding.drawerLayout.closeDrawer(GravityCompat.START)

    }
    private fun openPrivacyPage() {
        val uri = Uri.parse(PRIVACY_POLICY_LINK)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun showAbout() {
        val version = getAppVersion(applicationContext)
        Toast.makeText(this, version.toVersionText(), Toast.LENGTH_SHORT).show()
    }

    private fun String.toVersionText(): String = "Bunkbuddy $this"
    private fun getAppVersion(context: Context): String {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageName: String = context.packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Unknown"
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
            showSnackbar(view, getString(R.string.user_not_logged_in_message))
        }
    }

    private fun showSnackbar(view: View, message: String){
        Snackbar.make(view, message, 1000).show()
    }

    private fun applyTheme(){
        if(isDarkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    private fun changeTheme(){
        isDarkTheme = !isDarkTheme
        val editor = sharedPreferences.edit()
        editor.putBoolean(DARK_MODE_ENABLED, isDarkTheme)
        editor.apply()
        applyTheme()
    }
    fun hideBottomNav(){
        binding.bottomNav.visibility = View.GONE
    }

    fun showBottomNav(){
        binding.bottomNav.visibility = View.VISIBLE
    }
    companion object{
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
        const val PRIVACY_POLICY_LINK = "https://bunkbuddyprivacypolicy.blogspot.com/2023/12/privacy-policy-for-bunkbuddy.html"
        const val SHARED_PREF = "BunkBuddySharedPref"
        const val DARK_MODE_ENABLED = "dark_mode"
        const val NOTIFICATION_ENABLED = "notification_enabled"
    }
}