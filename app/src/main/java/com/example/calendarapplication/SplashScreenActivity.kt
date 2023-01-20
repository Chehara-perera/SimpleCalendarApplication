package com.example.calendarapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var image:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        image = findViewById(R.id.image)
        image.alpha = 0f
        image.animate().setDuration(1500).alpha(1f).withEndAction{
            val myintent= Intent(this,CalendarActivity::class.java)
            startActivity(myintent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()

        }


    }
}