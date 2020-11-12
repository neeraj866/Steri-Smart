package com.medical.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UsersModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "activeStatus") var activeStatus: Int,
    @ColumnInfo(name = "firstName") var firstName: String,
    @ColumnInfo(name = "lastName") var lastName: String,
    @ColumnInfo(name = "signature") var signature: String
)