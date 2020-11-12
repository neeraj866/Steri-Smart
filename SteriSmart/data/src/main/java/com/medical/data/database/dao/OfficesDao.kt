package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.OfficesModel

@Dao
interface OfficesDao {
    @Query("SELECT * FROM offices")
    fun getAllOffices(): List<OfficesModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOffice(Office: OfficesModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOffice(vararg Office: OfficesModel)
}