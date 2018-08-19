package com.android.lightmass.safely.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.Fade
import com.android.lightmass.safely.R

// constants
const val SPLASH_DURATION = 2000
class SplashScreen : AppCompatActivity() {

    /**
     * Global vars
     */

    /**
     * Functions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = Fade()
        setContentView(R.layout.activity_splash_screen)

        // set a timer for 3 seconds to show the splash screen
        object : CountDownTimer(SPLASH_DURATION.toLong(), COUNTDOWN_INTERVAL.toLong()) {
            override fun onFinish() {
                // when the timer ends start the main activity
                Intent(this@SplashScreen, MainActivity::class.java).also {
                    startActivity(it)
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                // do something
            }
        }.start()
    }

    //TODO stop activities from being able to be returned.
}
