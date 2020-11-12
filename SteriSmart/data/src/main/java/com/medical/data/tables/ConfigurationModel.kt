package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuration")
data class ConfigurationModel(@PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "lastDateUsed") val lastDateUsed: String,
    @ColumnInfo(name = "macAddress") val macAddress: String,
    @ColumnInfo(name = "officeId") val officeId: String)