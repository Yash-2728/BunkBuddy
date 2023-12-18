package com.tejasdev.bunkbuddy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.tejasdev.bunkbuddy.R

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        val tv = findViewById<TextView>(R.id.textView)
        Handler().postDelayed({
            nextActivity()
        }, 3000)

        val slogan = "Attendance tracking made easy!"
        val builder = StringBuilder()

        Thread{
            for (letter in slogan){
                builder.append(letter)
                Thread.sleep(100)
                runOnUiThread{
                    tv.text = builder.toString()
                }
            }
        }.start()

    }
    private fun nextActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}