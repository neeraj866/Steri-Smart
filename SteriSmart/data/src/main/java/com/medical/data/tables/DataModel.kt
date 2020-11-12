package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class DataModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "biLotNumber") var biLotNumber: String,
    @ColumnInfo(name = "biResult") var biResult: String,
    @ColumnInfo(name = "biTime") var biTime: String,
    @ColumnInfo(name = "biUserId") var biUserId: String,
    @ColumnInfo(name = "biUsername") var biUsername: String,
    @ColumnInfo(name = "biTestDate") var biTestDate: String,
    @ColumnInfo(name = "biTestTime") var biTestTime: String,
    @ColumnInfo(name = "bowieDickResult") val bowieDickResult: String,
    @ColumnInfo(name = "chemicalIndicator") val chemicalIndicator: String,
    @ColumnInfo(name = "cleanMethod") val cleanMethod: String,
    @ColumnInfo(name = "autoclave") var autoclave: String,
    @ColumnInfo(name = "cycleAutoclaveManufacturer") val cycleAutoclaveManufacturer: String,
    @ColumnInfo(name = "cycleAutoclaveModel") val cycleAutoclaveModel: String,
    @ColumnInfo(name = "cycleDate") val cycleDate: String,
    @ColumnInfo(name = "cycleDateFormatted") val cycleDateFormatted: Long,
    @ColumnInfo(name = "cycleLotNumber") val cycleLotNumber: String,
    @ColumnInfo(name = "cycleNumber") val cycleNumber: String,
    @ColumnInfo(name = "cyclePressure") val cyclePressure: String,
    @ColumnInfo(name = "pressureResult") val pressureResult: String,
    @ColumnInfo(name = "cycleTemperature") val cycleTemperature: String,
    @ColumnInfo(name = "temperatureResult") val temperatureResult: String,
    @ColumnInfo(name = "cycleTime") val cycleTime: String,
    @ColumnInfo(name = "cycleUserId") val cycleUserId: String,
    @ColumnInfo(name = "cycleUsername") val cycleUsername: String,
    @ColumnInfo(name = "packageString") var packageString: String,
    @ColumnInfo(name = "runTime") val runTime: String,
    @ColumnInfo(name = "typeFiveStatus") var typeFiveStatus: String,
    @ColumnInfo(name = "typeFourStatus") var typeFourStatus: String,
    @ColumnInfo(name = "typeOneStatus") var typeOneStatus: String,
    @ColumnInfo(name = "cycleResult") var cycleResult: String,
    @ColumnInfo(name = "packageInfo") var packageInfo: String
)