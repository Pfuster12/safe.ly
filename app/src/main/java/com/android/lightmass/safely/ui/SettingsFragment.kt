package com.android.lightmass.safely.ui


import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load the preferences from xml resource
        addPreferencesFromResource(R.xml.preferences)

        // bind the message summary to the value
        findPreference(getString(R.string.edit_default_alert_message)).setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as? String
            return@setOnPreferenceChangeListener true
        }
    }
}
