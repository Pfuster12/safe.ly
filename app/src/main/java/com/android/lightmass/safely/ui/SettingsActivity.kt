package com.android.lightmass.safely.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    /**
     * Global vars
     */

    /**
     * Functions
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = Slide()
        setContentView(R.layout.activity_settings)

        // set the back action icon
        back_action.setOnClickListener {
            onBackPressed()
        }

        // set the share action
        share_action_button.setOnClickListener {
            shareAction()
        }

        // set the welcome screen launch
        welcome_screens_action.setOnClickListener { _ ->
            Intent(this, WelcomeScreenActivity::class.java).also { startActivity(it) }
        }
    }

    /**
     * Helper fun to launch an intent chooser with share text
     */
    private fun shareAction() {
        // launch a chooser to share a text with the app link
        Intent(Intent.ACTION_SEND).setType("text/plain")
                // subject
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                // main text
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
                // send intent chooser
                .let {
                    startActivity(Intent.createChooser(it,
                            getString(R.string.share_chooser_title)))
                }
    }
}
