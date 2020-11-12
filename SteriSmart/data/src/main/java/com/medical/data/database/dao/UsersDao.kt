package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.UsersModel

@Dao
interface UsersDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UsersModel>

    @Query("SELECT * FROM users WHERE id == :userId")
    fun getUserById(userId: Int): UsersModel

    @Query("SELECT * FROM users WHERE activeStatus == :activeStatus")
    fun getActiveUser(activeStatus: Int): List<UsersModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: UsersModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(vararg user: UsersModel)

    @Delete
    fun deleteUser(user: UsersModel)
}