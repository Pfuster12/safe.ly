package com.android.lightmass.safely.pojos

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Contact pojo anonnated as Room entity
 */
@Entity(tableName = "contacts")
data class Contact(var name: String?,
              var mobile: String?) {

    // create an id outside the constructor so we don't need to pass it.
    @PrimaryKey(autoGenerate = true)
    var uid: Long? = null
}