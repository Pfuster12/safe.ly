package com.android.lightmass.safely.data

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import com.android.lightmass.safely.database.ContactsDao
import com.android.lightmass.safely.database.ContactsDatabase
import com.android.lightmass.safely.model.Contact
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Repository module responsible for handling data operations. Provides a clean API
 * to the rest of the app. Knows where to get the data from and what API calls to make when
 * data is updated. Mediates between different data sources (persistent
 * model, web service, etc.).
 */

const val DATA_CONTACT_NAME_KEY = "com.android.lightmass.safely.data.DATA_CONTACT_NAME_KEY"
const val DATA_CONTACT_NUMBER_KEY = "com.android.lightmass.safely.data.DATA_CONTACT_NUMBER_KEY"

class ContactsRepo {

    /**
     * Global variables
     */

    companion object {
        // worker thread to handle db calls
        private var executor: ExecutorService? = null

        // instance object of the repo
        private var repo: ContactsRepo? = null

        private var contactsDAO: ContactsDao? = null

        // init function for singleton pattern and worker thread instantiation
        fun getInstance(context: Context?): ContactsRepo {
            // check if instance is null, else
            return repo ?: synchronized(this) {
                // init a worker thread if null or destroyed,
                executor = executor ?: Executors.newSingleThreadExecutor()

                // init a contacts DAO,
                contactsDAO = contactsDAO ?: ContactsDatabase.getInstance(context).contactsDao()

                // and return the repo instance only if null in a thread safe way
                return@synchronized repo ?: ContactsRepo()
            }
        }
    }

    /**
     * Functions
     */

    /**
     * Helper method to set instance to null
     */
    fun destroyInstance() {
        repo = null
    }

    /**
     * Main function to grab the contacts from the database directly at activity/fragment creation
     */
    fun getContacts() = contactsDAO?.getAll() // gets live data directly from the database

    /**
     * function to save contact passed into the database in a worker thread
     */
    fun saveContact(contact: Contact?) {
        OneTimeWorkRequestBuilder<DatabaseWorker>()
                .setInputData(Data.Builder()
                        .putString(DATA_CONTACT_NAME_KEY, contact?.name)
                        .putString(DATA_CONTACT_NUMBER_KEY, contact?.mobile)
                        .build())
                .build()
                .let {
                    WorkManager.getInstance()?.enqueue(it)
                }
    }

    /**
     * WorkManager provides a clean API to do background work at an appropriate time.
     * If not specified the task will run as soon as possible
     */
    class DatabaseWorker : Worker() {

        /**
         * Override doWork to do save action. If the returned id is not -1 then operation
         * was a success.
         */
        override fun doWork() = if (contactsDAO?.save(getContactFromData())?.toInt() != -1)
            // return success if id is not -1, otherwise failure
            Result.SUCCESS else Result.FAILURE

        /**
         * Helper fun to make Contact pojo from worker input data
         */
        private fun getContactFromData() = Contact(
                inputData.getString(DATA_CONTACT_NAME_KEY, "Default"),
                inputData.getString(DATA_CONTACT_NUMBER_KEY, "n/a"))

    }
}