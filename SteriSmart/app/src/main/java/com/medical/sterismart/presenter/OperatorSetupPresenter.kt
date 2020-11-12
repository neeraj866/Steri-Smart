package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.UsersDao
import com.medical.data.tables.UsersModel
import com.medical.sterismart.view.OperatorSetupView
import javax.inject.Inject

class OperatorSetupPresenter @Inject constructor() {

    var operatorSetupView: OperatorSetupView? = null

    private var userModels: MutableList<UsersModel>? = null

    private lateinit var usersDao: UsersDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        usersDao = db.usersDao()
    }

    fun addOperator(userModels: UsersModel) {
        Thread {
            usersDao.addUser(userModels)
            operatorSetupView?.addedSuccess()
        }.start()
    }

    fun updateOperator(userModels: UsersModel) {
        Thread {
            usersDao.updateUser(userModels)
            operatorSetupView?.updateSuccess()
        }.start()
    }

    fun getOperatorNextId(): Int? {
        var nextId = 1
        when {
            usersDao.getAllUsers().isNotEmpty() -> {
                userModels = ArrayList()
                userModels?.addAll(usersDao.getAllUsers())
                nextId =
                    (userModels as ArrayList<UsersModel>)[(userModels as ArrayList<UsersModel>).size - 1].id + 1
            }
        }
        return nextId
    }

    fun deleteOperator(userModels: UsersModel) {
        Thread {
            usersDao.deleteUser(userModels)
            operatorSetupView?.deletedSuccess()
        }.start()
    }

    fun getActiveOperators(): MutableList<UsersModel>? {

        return when {
            usersDao.getAllUsers().isNotEmpty() -> {
                when {
                    userModels != null -> {
                        userModels?.clear()
                    }
                    else -> {
                        userModels = ArrayList()
                    }
                }
                userModels?.addAll(usersDao.getAllUsers())
                userModels
            }
            else -> {
                userModels
            }
        }
    }
}