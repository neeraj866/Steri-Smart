package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.PackagesModel

@Dao
interface PackagesDao {
    @Query("SELECT * FROM packages")
    fun getAllPackages(): List<PackagesModel>

    @Query("SELECT * FROM packages WHERE id == :id")
    fun getPackageById(id: Int): PackagesModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPackage(PackagesModel: PackagesModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePackage(vararg PackagesModel: PackagesModel)

    @Delete
    fun deletePackage(packagesModel: PackagesModel)
}