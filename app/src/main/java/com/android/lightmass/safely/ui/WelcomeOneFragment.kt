package com.android.lightmass.safely.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.fragment_welcome_one.view.*

/**
 * A simple [Fragment] subclass showing the first welcome screen explaining how the app works
 *
 */
class WelcomeOneFragment : Fragment() {

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

        // create a spannable,
        val spannable = SpannableString(getString(R.string.welcome_screen_1_text))

        // set the span for the letters
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)),0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)),134, 142, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // set the text,
        root.description_text.text = spannable
        return root
    }
}
