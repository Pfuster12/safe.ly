package com.android.lightmass.safely.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Custom [FrameLayout] to intercept dragging from its children and apply drag functionality
 * to the frame holding the Contacts fragment.
 */
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
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // grab details
                downRawX = event.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                // calculate change in coordinates of finger move,
                (event.rawX - downRawX).also {
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

    /**
     * Once the event is intercepted handle it in this function
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // grab the activity
        val activityContext = (context as MainActivity)
        // guideline x
        val openX = activityContext.open_contacts_drawer_guideline.x
        // range
        val range = screenWidth - openX
        // get add contact original y position
        val addContactY = screenHeight -
                activityContext.add_contact_action_button.height -
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        resources.getDimension(R.dimen.general_margin),
                        resources.displayMetrics)

        // cycle through events
        return when (event?.actionMasked) {
        // when finger is down
            MotionEvent.ACTION_DOWN -> {
                // get original coordinates
                downRawX = event.rawX
                true
            }
        // when finger moves,
            MotionEvent.ACTION_MOVE -> {
                // calculate change in coordinates of finger move
                val changeX = event.rawX - downRawX
                // get the dragged value
                val dragChange = changeX - oldXChange

                // change the x position of the layout with the drag
                if (dragChange > 0) {
                    when (this.x) {
                        in 0f..screenWidth -> {
                            this.x += dragChange
                            // change the alpha of the dark screen with the drag
                            activityContext.dark_screen.alpha -= (dragChange / range)
                        }
                    }
                } else if (dragChange < 0) {
                    when (this.x) {
                        in openX..screenWidth -> {
                            this.x += dragChange
                            // change the alpha of the dark screen with the drag
                            activityContext.dark_screen.alpha -= (dragChange / range)
                        }
                    }
                }

                // store the change
                oldXChange = changeX
                true
            }
            // when finger leaves screen,
            MotionEvent.ACTION_UP -> {
                val closingThreshold = screenWidth.minus(screenWidth/ 2.2).toFloat()
                when (this.x) {
                // if the drawer is dragged more than halfway,
                    in closingThreshold..screenWidth -> {
                        // animate it back out,
                        this.animate().x(screenWidth).start()
                        // and using the activity context
                        activityContext.apply {
                            // hide the dark screen with the frame leaving,
                            dark_screen.animate().alpha(0f).start()
                            // set the shown boolean to false
                            isContactsMenuShown = false
                            showAddContactButton()
                        }
                        // bring the add contact button back up with a spring anim,
                        SpringAnimation(activityContext.add_contact_action_button, DynamicAnimation.Y, addContactY)
                                .run {
                                    // set spring properties,
                                    spring.apply { dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY }
                                            .apply { stiffness = SpringForce.STIFFNESS_LOW }
                                    // and start the animation
                                    start()
                                }
                    }
                    // when it is not,
                    in -screenWidth..closingThreshold -> {
                        // animate fragment to the open guideline x position again,
                        SpringAnimation(this, DynamicAnimation.X, openX)
                                .run {
                                    // set spring properties,
                                    spring.apply { dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY }
                                            .apply { stiffness = SpringForce.STIFFNESS_LOW }
                                    // and start the animation
                                    start()
                                    // bring the alpha back to the dark screen
                                    activityContext.dark_screen.animate().alpha(0.7f).start()
                                }
                    }

                }
                oldXChange = 0f
                true
            }
            else -> true
        }
    }
}