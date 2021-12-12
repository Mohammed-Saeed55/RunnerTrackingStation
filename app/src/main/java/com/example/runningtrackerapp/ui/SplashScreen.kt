package com.example.runningtrackerapp.ui

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.example.runningtrackerapp.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        top.startAnimation(topAnimation)
        center.startAnimation(middleAnimation)
        bottom.startAnimation(bottomAnimation)

        val animDrawable = splash_activity.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(1000)
        animDrawable.start()

        val splashScreenTimeOut = 3000L
        val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)

        Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        }, splashScreenTimeOut)
    }
}
