package com.android.lightmass.safely.ui

import android.app.Activity
import android.app.LoaderManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import com.android.lightmass.safely.R
import kotlinx.android.synthetic.main.activity_main.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import com.android.lightmass.safely.pojos.Contact
import com.android.lightmass.safely.viewmodel.ContactsViewModel

// constants
const val REQUEST_SELECT_CONTACT = 1
const val LOADER_CONTACT_ID = 0
const val LOADER_URI_KEY = "com.android.lightmass.safely.ui.LOADER_URI_KEY"

/**
 * This class is concerned with the logic of the main activity UI. The big, red,
 * 'safe.ly' button works here. There is also a share action, and an add contact button
 * in the button. The contacts menu slides from the right when the button is pressed, however
 * the logic for its ui lives in another fragment.
 */
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Global variables
     */

    // the contacts list data -not null as prefer to have 0 entries.
    private var contacts: MutableList<Contact>? = null

    // view model instance
    private var viewModel: ContactsViewModel? = null

    // contacts menu showing boolean
    var isContactsMenuShown = false
    private var isAddContactShown = true

    // screen properties
    private var screenWidth = 0f
    private var screenHeight = 0f

    // add contact button y property
    private var addContactY = 0f

    // projection with the Contact tables name and number columns
    private val PROJECTION = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER)

    /**
     * Functions
     */

    /*
     * The on create of the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // grab the screen width and height
        DisplayMetrics().also { windowManager.defaultDisplay.getMetrics(it) }
                .run {
                    screenWidth = widthPixels.toFloat()
                    screenHeight = heightPixels.toFloat()
                }

        // set the view model to observe the contacts live data
        subscribeToModel()

        /*
         * Add contact button call the action of adding a contact item to the database
         * by picking a contact from your existing contacts or adding a new one manually.
         */
        add_contact_action_button.setOnClickListener {
            // init intent and assign type
            Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI).let {
                    // using the intent,
                        if (it.resolveActivity(packageManager) != null) {
                            startActivityForResult(it, REQUEST_SELECT_CONTACT)
                        }
                    }
        }

        /*
         * Contact menu button handles sliding the contacts list from the right
         * of the screen. User can add a contact from here or delete one.
         */

        // hide contact menu to the side screen
        contacts_fragment_frame.x = screenWidth

        // when the contacts menu is pressed,
        contacts_menu_action_button.setOnClickListener {
            // slide a drawer from the right animating to the guideline,
            showContactsDrawer()
            //hide the add contact button,
            showAddContactButton()
        }

        /*
         * Slide back button pulls the contact menu out of the screen again.
         */

        // when the slide back button is pressed,
        slide_contacts_action.setOnClickListener {
            // animate the fragment out of the screen
            showContactsDrawer()
            // show the add contact button,
            showAddContactButton()
        }

        /*
         * Share action button sends a link through an intent
         */
        share_action_button.setOnClickListener {
            shareAction()
        }

        //onTouchDrawerFunctionality()
    }

    /**
     * Helper fun to initialize the view model and observe the contacts live data held
     * in the model observing changes to the database.
     */
    private fun subscribeToModel() {
        // init the list
        contacts = mutableListOf()
        // create the view model and init the global variable,
        viewModel = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
                // init the repo,
        viewModel?.init(this)
                // get the live data instance,
                ?.getContacts()
                // observe the live data changes,
                ?.observe(this, Observer { data: List<Contact>? ->
                    // on the local contacts list,
                    contacts?.apply {
                        //clear the list first,
                        clear()
                        // and add new contacts to it
                        addAll(data ?: listOf())
                    }
                })
    }

    /**
     * Helper fun to show/hide contacts drawer by sliding a drawer out and displaying a
     * [ContactsFragment].
     */
    private fun showContactsDrawer() {
        showDarkScreen()
        // check if contacts fragment drawer is shown,
        isContactsMenuShown = if (!isContactsMenuShown) {
            // animate fragment to the open guideline x position,
            SpringAnimation(contacts_fragment_frame, DynamicAnimation.X, open_contacts_drawer_guideline.x)
                    .run {
                        // set spring properties,
                        spring.apply { dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY }
                                .apply { stiffness = SpringForce.STIFFNESS_LOW }
                        // and start the animation
                        start()
            }

            // set the boolean to true
            true
        } else {
            // animate the fragment out of the screen
            contacts_fragment_frame.animate().x(screenWidth).start()
            // set the boolean to false
            false
        }
    }

    /**
     * Convenience fun to darken screen or hide it according to the contacts drawer opening
     */
    private fun showDarkScreen() {
        // check if drawer is open
        if (!isContactsMenuShown) dark_screen.animate().alpha(0.7f).start() // darken screen
        // else hide it
        else dark_screen.animate().alpha(0f).start()
    }

/*
    private fun onTouchDrawerFunctionality() {
        // init vars for the original touch coordinates
        var downRawX = 0f

        // var to store previous y change in coordinates
        var oldXChange = 0f

        contacts_fragment_frame.setOnTouchListener { v, event ->
            // cycle through events
            when (event?.actionMasked) {
            // when finger is down
                MotionEvent.ACTION_DOWN -> {
                    // get original coordinates
                    downRawX = event.rawX
                    return@setOnTouchListener true
                }
            // when finger moves,
                MotionEvent.ACTION_MOVE -> {
                    // calculate change in coordinates of finger move
                    val xChange = event.rawX - downRawX
                    Log.e("DRAWERDRAG", (xChange - oldXChange).toString())
                    val dragChange = xChange - oldXChange
                    contacts_fragment_frame.x += dragChange
                    // set the current xChange to the old change for next move action
                    oldXChange = xChange
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener true
            }
        }
    }*/

    /**
     * Helper fun to show/hide add contacts button with a spring anim
     */
    private fun showAddContactButton() {
        // get the original y position of the add contact button by adding the margin
        // plus its height
        addContactY = screenHeight -
                add_contact_action_button.height -
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        resources.getDimension(R.dimen.general_margin),
                        resources.displayMetrics)

        // check if button is shown,
        isAddContactShown = if (isAddContactShown) {
            // if it is hide it,
            // animate fragment to the guideline x position
            SpringAnimation(add_contact_action_button, DynamicAnimation.Y, screenHeight)
                    .start()
            // set it to now showing
            false
        } else {
            // otherwise bring it back up with a spring anim,
            SpringAnimation(add_contact_action_button, DynamicAnimation.Y, addContactY)
                    .run {
                        // set spring properties,
                        spring.apply { dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY }
                                .apply { stiffness = SpringForce.STIFFNESS_LOW }
                        // and start the animation
                        start()
            }

            // and set it to showing
            true
        }
    }

    /**
     * Helper fun to launch an intent chooser with share text
     */
    private fun shareAction() {
        // launch a chooser to share a text with the app link
        Intent(Intent.ACTION_SEND).setType("text/plain")
                // subject
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                // main text
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
                // send intent chooser
                .let {
                    startActivity(Intent.createChooser(it,
                            getString(R.string.share_chooser_title)))
                }
    }

    /**
     * Override to grab the contact data the user picks to add information
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // first, check if the result sent back was requested by the contact picker
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == Activity.RESULT_OK) {
            /* if it is start the loader to async load the contact data with a cursor
            * passing in the uri as a bundle to the args */
            loaderManager.restartLoader(LOADER_CONTACT_ID,
                    // pass in a bundle with the uri as data
                    Bundle().apply {
                        putString(LOADER_URI_KEY, data?.data.toString())
                    },
                    // the implemented callbacks below
                    this)
        }
    }

    /**
     * CursorLoader implementation to manage async cursor call to the Contacts database.
     * Handled in the MainActivity class because a non static context object is needed.
     */

    // on create provide the projection for the number and phone and contact uri
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // if id matches,
        return when (id) {
            LOADER_CONTACT_ID -> {
                // then return a cursor loader with the uri passed through the args
               CursorLoader(this,
                       Uri.parse(args?.getString(LOADER_URI_KEY)),
                       // pass the projection asking for name and number
                       PROJECTION,
                       null,
                       null,
                       null)
            }
            else -> Loader(this)
        }
    }

    // on load finished, handle the cursor data to insert into the database
    override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
        // check id,
        when (loader?.id) {
            // if it is the contact loader,
            LOADER_CONTACT_ID -> {
                /* Grab the contact picked by a user from the Contacts db.
                 * This is done in the main activity because a context instance is needed
                 * to access the content resolver. */
                cursor?.let {
                    it.moveToFirst()
                    Log.e("CURSOR", it.getString(it.getColumnIndex(Phone.DISPLAY_NAME)))
                    Contact(it.getString(it.getColumnIndex(Phone.DISPLAY_NAME)),
                            it.getString(it.getColumnIndex(Phone.NUMBER)))
                }.also {
                    viewModel?.saveContact(it)
                }
            }
        }
    }

    // on loader reset clean any references to old data since cursor has been updated
    override fun onLoaderReset(loader: Loader<Cursor>?) {
        // reset
    }
}
