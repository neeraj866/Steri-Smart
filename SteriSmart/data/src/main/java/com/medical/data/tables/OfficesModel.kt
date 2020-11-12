package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offices")
data class OfficesModel(@PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "backupLocation") val backupLocation: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "endDate") var endDate: String,
    @ColumnInfo(name = "labelCount") val labelCount: Int,
    @ColumnInfo(name = "licenseNumber") var licenseNumber: String,
    @ColumnInfo(name = "officeId") val officeId: String,
    @ColumnInfo(name = "officeName") val officeName: String,
    @ColumnInfo(name = "pin") val pin: String,
    @ColumnInfo(name = "postalCode") val postalCode: String,
    @ColumnInfo(name = "province") val province: String,
    @ColumnInfo(name = "manager_initials") val managerInitials: String,
    @ColumnInfo(name = "startDate") var startDate: String,
    @ColumnInfo(name = "streetAddress") val streetAddress: String,
    @ColumnInfo(name = "telephone") val telephone: String,
    @ColumnInfo(name = "unitNumber") val unitNumber: Int)