package com.medical.sterismart.presenter

import android.content.Context
import android.text.TextUtils
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.DataDao
import com.medical.data.database.dao.UsersDao
import com.medical.data.tables.DataModel
import com.medical.data.tables.UsersModel
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.view.BITestView
import javax.inject.Inject

class BITestPresenter @Inject constructor() {

    var biTestView: BITestView? = null


    private lateinit var dataDao: DataDao
    private lateinit var userDao: UsersDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        dataDao = db.dataModelDao()
        userDao = db.usersDao()
    }

    fun updateData(dataModel: DataModel) {
        dataModel.biTestDate = Utility.getCurrentDate()
        dataModel.biTestTime = Utility.getCurrentTime()
        Thread {
            dataDao.updateData(dataModel)
            when {
                TextUtils.equals(dataModel.biResult, "Pass") -> {
                    val dataList = getAllDataModelsByDate(dataModel.cycleDate)
                    when {
                        dataList != null -> {
                            for (item in dataList.indices) {
                                when {
                                    TextUtils.equals(
                                        dataList[item].autoclave,
                                        dataModel.autoclave
                                    ) -> {
                                        dataList[item].biTestDate = Utility.getCurrentDate()
                                        dataList[item].biTestTime = Utility.getCurrentTime()
                                        dataList[item].biResult = "Pass"
                                        dataList[item].biLotNumber = dataModel.biLotNumber
                                        dataList[item].biUserId = dataModel.biUserId
                                        dataDao.updateData(dataList[item])
                                    }
                                }
                                when (item) {
                                    (dataList.size - 1) -> {
                                        biTestView?.updateSuccess()
                                    }
                                }
                            }
                        }
                        else -> {
                            biTestView?.updateSuccess()
                        }
                    }
                }
                else -> {
                    biTestView?.updateSuccess()
                }
            }
        }.start()
    }

    fun getUsers(): MutableList<UsersModel>? {
        var usersModel: MutableList<UsersModel>? = null

        return when {
            userDao.getAllUsers().isNotEmpty() -> {
                when {
                    usersModel != null -> {
                        usersModel.clear()
                    }
                    else -> {
                        usersModel = ArrayList()
                    }
                }
                usersModel.addAll(userDao.getAllUsers())
                usersModel
            }
            else -> {
                usersModel
            }
        }
    }


    private fun getAllDataModelsByDate(date: String): List<DataModel>? {
        var dataModels: MutableList<DataModel>? = null
        return when {
            dataDao.getDataByDateAndPendingBITest(date, "Pending").isNotEmpty() -> {
                when {
                    dataModels != null -> {
                        dataModels.clear()
                    }
                    else -> {
                        dataModels = ArrayList()
                    }
                }
                dataModels.addAll(dataDao.getDataByDateAndPendingBITest(date, "Pending"))
                dataModels
            }
            else -> {
                dataModels
            }
        }
    }

     fun getDataModel(id: Int): DataModel? {
        return dataDao.getDataById(id)
    }

    fun getAllDataModels(date: String): List<DataModel>? {
        var dataModels: MutableList<DataModel>? = null
        return when {
            dataDao.getDataByDateAndPendingBITest(date, "Pending", date).isNotEmpty() -> {
                when {
                    dataModels != null -> {
                        dataModels.clear()
                    }
                    else -> {
                        dataModels = ArrayList()
                    }
                }
                dataModels.addAll(dataDao.getDataByDateAndPendingBITest(date, "Pending", date))
                dataModels
            }
            else -> {
                dataModels
            }
        }
    }
}