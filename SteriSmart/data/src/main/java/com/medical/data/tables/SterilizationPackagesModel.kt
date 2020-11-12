package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sterilization_packages")
data class SterilizationPackagesModel(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "packageName") var packageName: String,
    @ColumnInfo(name = "packageType") val packageType: String
)
