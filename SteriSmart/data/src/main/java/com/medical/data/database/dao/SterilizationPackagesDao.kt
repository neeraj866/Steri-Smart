package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.SterilizationPackagesModel

@Dao
interface SterilizationPackagesDao {
    @Query("SELECT * FROM sterilization_packages")
    fun getAllSterilizationPackages(): List<SterilizationPackagesModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSterilizationPackage(PackagesModel: SterilizationPackagesModel): Long

    @Delete
    fun deletePackage(packagesModel: SterilizationPackagesModel)

    @Query("DELETE FROM sterilization_packages")
    fun nukeTable()
}