package com.android.lightmass.safely.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.android.lightmass.safely.pojos.Contact

/**
 * Room database class, creates db file and gives access to the database dao (data access object).
 */
@Database(entities = [Contact::class], version = 1)
abstract class ContactsDatabase : RoomDatabase() {

    // required abstract Dao function for Room
    abstract fun contactsDao(): ContactsDao

    /**
     * @googledocs "You should follow the singleton design pattern when instantiating an
     * AppDatabase object, as each RoomDatabase instance is fairly expensive, and you rarely need
     * access to multiple instances."
     * @synchronized() provides protection by ensuring that a crucial section of the code is never
     * executed concurrently by two different threads, ensuring data consistency.
     */
    companion object {
        private var INSTANCE: ContactsDatabase? = null

        /**
         * instance function initializing the database synchronized with threads.
         */
        fun getInstance(context: Context?) =
                // check if null,
                INSTANCE ?: synchronized(this) {
                    // if it is, check again concurrently and init the build function if still null
                    INSTANCE ?: Room.databaseBuilder(context?.applicationContext!!,
                            ContactsDatabase::class.java,
                            "contacts")
                            .build()
                }
    }

    /**
     * Helper fun to destroy instance
     */
    fun destroyInstance() {
        INSTANCE = null
    }
}