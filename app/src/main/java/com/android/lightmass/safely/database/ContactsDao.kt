package com.android.lightmass.safely.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.android.lightmass.safely.pojos.Contact

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contacts")
    fun getAll(): LiveData<List<Contact>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(contact: Contact?): Long?

    @Query("SELECT * FROM contacts")
    fun getList(): List<Contact>?
}