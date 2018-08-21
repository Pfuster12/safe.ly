package com.android.lightmass.safely.ui


import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment

import com.android.lightmass.safely.R

/**
 * A simple [PreferenceFragment] subclass for the preference activity
 *
 */
class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load the preferences from xml resource
        addPreferencesFromResource(R.xml.preferences)

        // bind the message summary to the value
        val messagePreference = findPreference(getString(R.string.edit_default_alert_message)) as EditTextPreference
        messagePreference.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as? String
            return@setOnPreferenceChangeListener true
        }

        // set the default value revert preference
        findPreference(getString(R.string.default_text_value)).setOnPreferenceClickListener {
            // set the message preferences back to the default
           messagePreference.summary = getString(R.string.prefix_alert_text)
            messagePreference.text = getString(R.string.prefix_alert_text)
            return@setOnPreferenceClickListener true
        }
    }
}
