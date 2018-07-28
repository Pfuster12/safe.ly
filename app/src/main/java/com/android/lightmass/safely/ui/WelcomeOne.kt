package com.android.lightmass.safely.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R

/**
 * A simple [Fragment] subclass showing the first welcome screen explaining how the app works
 *
 */
class WelcomeOne : Fragment() {

    /**
     * Global vars
     */

    /**
     * Functions
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_welcome_one, container, false)

        return root
    }
}
