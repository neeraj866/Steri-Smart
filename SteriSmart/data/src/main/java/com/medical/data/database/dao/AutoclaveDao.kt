package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.AutoclaveModel

@Dao
interface AutoclaveDao {
    @Query("SELECT * FROM autoclave")
    fun getAllAutoclaves(): List<AutoclaveModel>

    @Query("SELECT * FROM autoclave WHERE id == :AutoclaveId")
    fun getAutoclaveById(AutoclaveId: Int): AutoclaveModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAutoclave(Autoclave: AutoclaveModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAutoclave(vararg Autoclave: AutoclaveModel)

    @Delete
    fun deleteAutoclave(autoclaveModel: AutoclaveModel)
}