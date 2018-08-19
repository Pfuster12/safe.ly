package com.android.lightmass.safely.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.android.lightmass.safely.data.ContactsRepo
import com.android.lightmass.safely.model.Contact

/**
 * [ViewModel] subclass handles providing the ui with the live data instance holding
 * the apps data, in this case a list of Contact pojos defined in this app.
 */
class ContactsViewModel : ViewModel() {

    // the live data being observed
    private val contacts: LiveData<List<Contact>>? = null
    // repo instance
    private var repo: ContactsRepo? = null

    /**
     * init function. The ViewModel is instantiated through a different pattern in the
     * MainActivity, this is just to init the repo instance for the getter methods.
     */
    fun init(context: Context?): ContactsViewModel {
        // init the repo if null,
        repo = repo ?: ContactsRepo.getInstance(context)
        // and return this for function chaining
        return this
    }

    /**
     * getter for [LiveData] object
     */
    fun getContacts() = repo?.getContacts()

    /**
     * Save a contact to the database
     */
    fun saveContact(contact: Contact?) {
        repo?.saveContact(contact)
    }
}