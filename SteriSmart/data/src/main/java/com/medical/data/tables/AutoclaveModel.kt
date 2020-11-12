package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autoclave")
data class AutoclaveModel(
        @PrimaryKey(autoGenerate = true) var id: Int,
        @ColumnInfo(name = "activeStatus") val activeStatus: Int,
        @ColumnInfo(name = "autoclaveType") val autoclaveType: String,
        @ColumnInfo(name = "manufacturer") val manufacturer: String,
        @ColumnInfo(name = "model") val model: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "image") val image: Int,
        @ColumnInfo(name = "maximum_temperature") val maximum_temperature: String,
        @ColumnInfo(name = "temperature_unit") val temperature_unit: String,
        @ColumnInfo(name = "maximum_pressure") val maximum_pressure: String,
        @ColumnInfo(name = "pressure_unit") val pressure_unit: String,
        @ColumnInfo(name = "duration") val duration: String,
        @ColumnInfo(name = "cycle_number") var cycleNumber: String,
        @ColumnInfo(name = "loaner") val isLoaner: Int
)