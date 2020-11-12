package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.*
import com.medical.data.tables.*
import com.medical.sterismart.view.SterilizationView
import javax.inject.Inject

class SterilizationPresenter @Inject constructor() {

    var sterilizationView: SterilizationView? = null

    private var packagesModels: MutableList<PackagesModel>? = null
    private var sterilizationPackagesModels: MutableList<SterilizationPackagesModel>? = null
    private var dataModels: MutableList<DataModel>? = null

    private lateinit var packagesDao: PackagesDao
    private lateinit var dataDao: DataDao
    private lateinit var userDao: UsersDao
    private lateinit var autoclaveDao: AutoclaveDao
    private lateinit var db: AppDatabase
    private var context: Context? = null
    private lateinit var sterilizationPackagesDao: SterilizationPackagesDao


    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        packagesDao = db.packagesDao()
        dataDao = db.dataModelDao()
        userDao = db.usersDao()
        autoclaveDao = db.autoclaveDao()
        sterilizationPackagesDao = db.sterilizationPackagesDao()
    }

    fun addData(dataModel: DataModel) {
        Thread {
            dataDao.addDate(dataModel)
            sterilizationView?.addedSuccess()
        }.start()
    }

    fun getDataNextId(): Int? {
        var nextId = 1
        when {
            dataDao.getAllData().isNotEmpty() -> {
                dataModels = ArrayList()
                dataModels?.addAll(dataDao.getAllData())
                nextId =
                    (dataModels as ArrayList<DataModel>)[(dataModels as ArrayList<DataModel>).size - 1].id + 1
            }
        }
        return nextId
    }

    fun getDataByPass(date: String, autoclave: String): DataModel? {
        var datamodel: DataModel? = null
        when {
            dataDao.getDataByDateAndPassBITest(date, autoclave, "Pass").isNotEmpty() -> {
                datamodel = dataDao.getDataByDateAndPassBITest(date, autoclave, "Pass")[0]
            }
        }
        return datamodel
    }

    fun getDataByDate(date: String, autoclave: String): MutableList<DataModel> {
        val dataModels: MutableList<DataModel> = ArrayList()
        return when {
            dataDao.getDataByDateAndPassBITest(date, autoclave, "Pass").isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByDateAndPassBITest(date, autoclave, "Pass"))
                dataModels
            }
            else -> {
                dataModels
            }
        }
    }

    fun deleteTable() {
        Thread {
            sterilizationPackagesDao.nukeTable()
        }.start()
    }

    fun getSterilizationPackages(): MutableList<SterilizationPackagesModel>? {
        return when {
            sterilizationPackagesDao.getAllSterilizationPackages().isNotEmpty() -> {
                when {
                    packagesModels != null -> {
                        sterilizationPackagesModels?.clear()
                    }
                    else -> {
                        sterilizationPackagesModels = ArrayList()
                    }
                }
                sterilizationPackagesModels?.addAll(sterilizationPackagesDao.getAllSterilizationPackages())
                sterilizationPackagesModels
            }
            else -> {
                sterilizationPackagesModels
            }
        }
    }


    fun updateAutoclave(autoclaveModel: AutoclaveModel) {
        Thread {
            autoclaveDao.updateAutoclave(autoclaveModel)
        }.start()
    }

    fun getPackages(): MutableList<PackagesModel>? {
        return when {
            packagesDao.getAllPackages().isNotEmpty() -> {
                when {
                    packagesModels != null -> {
                        packagesModels?.clear()
                    }
                    else -> {
                        packagesModels = ArrayList()
                    }
                }
                packagesModels?.addAll(packagesDao.getAllPackages())
                packagesModels
            }
            else -> {
                packagesModels
            }
        }
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

    fun getAutoclaves(): MutableList<AutoclaveModel>? {
        var autoclaveModel: MutableList<AutoclaveModel>? = null
        return when {
            autoclaveDao.getAllAutoclaves().isNotEmpty() -> {
                when {
                    autoclaveModel != null -> {
                        autoclaveModel.clear()
                    }

                    else -> {
                        autoclaveModel = ArrayList()
                    }
                }
                autoclaveModel.addAll(autoclaveDao.getAllAutoclaves())
                autoclaveModel
            }
            else -> {
                autoclaveModel
            }
        }
    }
}