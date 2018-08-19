package com.android.lightmass.safely.ui


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.fragment_welcome_four.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class WelcomeFourFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_welcome_four, container, false)

        // tap on the screen to go to the main activity
        root.welcome_container.setOnClickListener {
            Intent(context, MainActivity::class.java).also { startActivity(it) }
        }

        return root
    }

}
