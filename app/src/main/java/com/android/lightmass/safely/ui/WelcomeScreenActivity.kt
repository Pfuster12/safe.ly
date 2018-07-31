package com.android.lightmass.safely.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.activity_welcome_screen.*

// constants
const val WELCOME_SCREENS = 4

class WelcomeScreenActivity : AppCompatActivity() {

    /**
     * Global vars
     */

    /**
     * Functions
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        // set the adapter,
        view_pager.adapter = WelcomeViewPager(supportFragmentManager)

        // set with viewpager,
        tab_layout.setupWithViewPager(view_pager)

        // set the tab icons to be the tab selector drawable
        setTabs()
    }

    /**
     * Convenience method to set the tab icons
     */
    private fun setTabs() {
        // cycle through tabs,
        for (i in 0..tab_layout.tabCount) {
            // and set the tabs to a selector
            tab_layout.getTabAt(i)?.setIcon(R.drawable.tab_selector)

        }
    }

    /**
     * Inner view pager class for the fragments showing the welcome information
     */
    class WelcomeViewPager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        // get fragments to show
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> WelcomeOneFragment()
                1 -> WelcomeTwoFragment()
                2 -> WelcomeThreeFragment()
                3 -> WelcomeFourFragment()
                else -> WelcomeOneFragment()
            }
        }

        // show the number of fragments
        override fun getCount() = WELCOME_SCREENS
    }
}
