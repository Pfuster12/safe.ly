package com.android.lightmass.safely.ui

import android.content.Context
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*

class MyFragmentFrameLayout : FrameLayout {

    // init the touch slop value
    private var touchSlop: Float = 40f

    // screen properties
    private var screenWidth = 0f
    private var screenHeight = 0f

    // init vars for the original touch coordinates
    private var downRawX = 0f

    // var to store previous y change in coordinates
    private var oldXChange = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context,
                attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context,
                attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val activityContext = (context as MainActivity)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        // grab the screen width and height
        DisplayMetrics().also { activityContext.windowManager.defaultDisplay.getMetrics(it) }
                .run {
                    screenWidth = widthPixels.toFloat()
                    screenHeight = heightPixels.toFloat()
                }
    }

    /**
     * Intercept touch event to scroll drawer out
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // grab details
                downRawX = ev.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                // calculate change in coordinates of finger move,
                (ev.rawX - downRawX).also {
                    // store this X change to store for the next drag,
                    oldXChange = it
                }.let {
                    // check if it is bigger than a touch slop to intercept a drag
                    if (it > touchSlop) return true
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // grab the activity
        val activityContext = (context as MainActivity)
        // guideline x
        val openX = activityContext.open_contacts_drawer_guideline.x

        // cycle through events
        when (event?.actionMasked) {
        // when finger is down
            MotionEvent.ACTION_DOWN -> {
                // get original coordinates
                downRawX = event.rawX
                return true
            }
        // when finger moves,
            MotionEvent.ACTION_MOVE -> {
                // calculate change in coordinates of finger move
                val xChange = event.rawX - downRawX
                Log.e("DRAWERDRAG", (xChange - oldXChange).toString())
                val dragChange = xChange - oldXChange
                this.x += dragChange

                // set the current xChange to the old change for next move action
                oldXChange = xChange
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (x > (screenWidth.let { it.minus(it / 3) })) {
                    this.animate().x(screenWidth).start()
                    activityContext.dark_screen.animate().alpha(0f).start()
                    activityContext.isContactsMenuShown = false
                } else {
                    // animate fragment to the open guideline x position,
                    SpringAnimation(this, DynamicAnimation.X, openX)
                            .run {
                                // set spring properties,
                                spring.apply { dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY }
                                        .apply { stiffness = SpringForce.STIFFNESS_LOW }
                                // and start the animation
                                start()
                            }
                }
                return true
            }
            else -> return true
        }
    }
}