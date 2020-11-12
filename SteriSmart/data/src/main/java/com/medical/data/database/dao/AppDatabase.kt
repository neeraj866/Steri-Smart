package com.medical.data.database.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.medical.data.tables.*

@Database(
    entities = [AutoclaveModel::class, ConfigurationModel::class, DataModel::class, OfficesModel::class, PackagesModel::class, UsersModel::class, SterilizationPackagesModel::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun autoclaveDao(): AutoclaveDao
    abstract fun configurationDao(): ConfigurationDao
    abstract fun dataModelDao(): DataDao
    abstract fun officesDao(): OfficesDao
    abstract fun packagesDao(): PackagesDao
    abstract fun sterilizationPackagesDao(): SterilizationPackagesDao
    abstract fun usersDao(): UsersDao

}