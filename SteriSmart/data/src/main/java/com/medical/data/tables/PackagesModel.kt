package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class PackagesModel(@PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "instruments") var instruments: String,
    @ColumnInfo(name = "packageName") val packageName: String,
    var isSelected: Boolean,
    var packageCount: Int,
    var packageBarcode: String,
    var packageType: String) {
    fun instrumentName(): String {
        return instruments
    }
}
