package com.android.lightmass.safely.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R


/**
 * A simple [Fragment] subclass.
 *
 */
class WelcomeThreeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_three, container, false)
    }


}
