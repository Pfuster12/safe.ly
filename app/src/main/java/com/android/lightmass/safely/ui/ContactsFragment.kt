package com.android.lightmass.safely.ui

import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.android.lightmass.safely.R
import com.android.lightmass.safely.model.Contact
import kotlinx.android.synthetic.main.fragment_contacts.view.*
import com.android.lightmass.safely.viewmodel.ContactsViewModel
import kotlinx.android.synthetic.main.fragment_contacts.*


/**
 * Top level constants
 */
// item types constant
const val CONTACT_ITEM_TYPE: Long = 101

/**
 * A simple [Fragment] subclass. The fragment displays a list of emergency contacts
 * the user adds or picks from their own contacts. User can delete or add a contact from here.
 *
 */
class ContactsFragment : Fragment() {

    /**
     * Global variables
     */

    // the contacts list data
    private var contacts: MutableList<Contact>? = null

    // adapter for list view
    private var adapter: BaseAdapter? = null

    // create a companion function for instance creation
    companion object {
        // make the fragment a val,
        private var contactsFragment: ContactsFragment? = null

        // create an instance of it only if it is null
        fun createInstance() = contactsFragment ?: ContactsFragment()
    }

    /**
     * Functions
     */

    /**
     * The on create of the fragment view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater
            // inflate the layout of the root,
            .inflate(R.layout.fragment_contacts,
                    container,
                    false).let { root: View? ->
                // init the contacts list,
                contacts = mutableListOf()

                // set adapter with helper function,
                setAdapter(root)
                return@let root
            }

    /**
     * override onResume to subscribe to the view model so the ViewModel instance
     * is created first in the MainActivity rather than the fragment, allowing for the
     * activity-owned instance to init the repo singleton.
     */
    override fun onResume() {
        super.onResume()
        // subscribe to the activity view model
        subscribeToModel()
    }

    /**
     * Helper fun to suscribe to view model holding live data of contacts list
     */
    private fun subscribeToModel() {
        // get an instance of the activity view model,
        ViewModelProviders.of(requireActivity())
                // of the contacts view model,
                .get(ContactsViewModel::class.java)
                // grab the live data from the database,
                .getContacts()
                // observe it...
                ?.observe(this, Observer { data ->
                    // when it changes, on the local contacts list,
                    contacts?.apply {
                        //clear the list first,
                        clear()
                        // and add new contacts to it,
                        addAll(data ?: listOf(Contact("Example Number",
                                "07490776869")))
                        // finally notify the data change
                        adapter?.notifyDataSetChanged()
                        // hide the empty view
                        empty_view.visibility = View.GONE
                    }
                    // show empty view if data is empty
                    if (data == null || data.isEmpty()) {empty_view.visibility = View.VISIBLE}
                })
    }

    /**
     * Helper fun to set list view adapter, grabbing data from the contacts list.
     * ListView is used instead of RecyclerView because no scrolling is necessary,
     * allowing a simpler implementation.
     */
    private fun setAdapter(root: View?) {
        // set the list view adapter of the fragment, initializing an anonymous adapter,
        adapter = object : BaseAdapter() {
            // overriding its functions,
            // get list count
            override fun getCount() =
                // return a blank item if null
                contacts?.size ?: 1

            // get item type, could be footer or item
            override fun getItemId(position: Int) = CONTACT_ITEM_TYPE

            // grab item from list to add into view
            override fun getItem(position: Int) =
                // return a blank item if null
                contacts?.get(position) ?: Contact("", "")

            // grab the list item view layout
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
                convertView ?: layoutInflater.inflate(R.layout.contact_list_item, parent, false)
                        .apply {
                            val currentItem = getItem(position)
                            // grab the name textview from list item & set the text of current name,
                            findViewById<TextView>(R.id.contact_name_title)?.text =
                                    getString(R.string.name_placeholder_string,
                                            currentItem.name)
                            // grab the phone textview and set the text of the current phone
                            findViewById<TextView>(R.id.contact_number)?.text =
                                    getString(R.string.number_placeholder_string,
                                            currentItem.mobile)
                            // apply function returns the recycled view
                        }
        }
        root?.contacts_list_view?.adapter = adapter

        // if contacts is empty show the empty view
        if (contacts == null || (contacts as MutableList).isEmpty()) { root?.empty_view?.visibility = View.VISIBLE}
    }
}
