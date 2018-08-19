package com.android.lightmass.safely.ui


import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R

/**
 * A simple [Fragment] subclass.
 *
 */
class WelcomeTwoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_welcome_two, container, false)

        //TODO check if permission is given before requesting.
        ActivityCompat.requestPermissions(activity as WelcomeScreenActivity, arrayOf(Manifest.permission.SEND_SMS),1)

        return root
    }


}
