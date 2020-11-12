package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.*
import com.medical.data.tables.AutoclaveModel
import com.medical.data.tables.DataModel
import com.medical.data.tables.PackagesModel
import com.medical.data.tables.UsersModel
import com.medical.sterismart.R
import com.medical.sterismart.view.AuditView
import javax.inject.Inject

class AuditPresenter @Inject constructor() {

    var auditView: AuditView? = null

    private var packagesModels: MutableList<PackagesModel>? = null
    private var dataModels: MutableList<DataModel>? = null

    private lateinit var packagesDao: PackagesDao
    private lateinit var dataDao: DataDao
    private lateinit var officesDao: OfficesDao
    private lateinit var userDao: UsersDao
    private lateinit var autoclaveDao: AutoclaveDao
    private lateinit var db: AppDatabase
    private var context: Context? = null


    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        packagesDao = db.packagesDao()
        dataDao = db.dataModelDao()
        userDao = db.usersDao()
        autoclaveDao = db.autoclaveDao()
        officesDao = db.officesDao()
    }

    fun getDataByPackage(packageName: String, startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        val finalPackageName = "%${packageName}%"
        when {
            dataDao.getDataByPackage(finalPackageName, startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByPackage(finalPackageName, startDate, endDate))
                auditView?.generateReport(dataModels)
            }
            else -> {
                auditView?.generateReport(dataModels)
            }
        }
    }

    fun getDataByAutoclave(autoclaveName: String, startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByAutoclave(autoclaveName, startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByAutoclave(autoclaveName, startDate, endDate))
                auditView?.generateReport(dataModels)
            }
            else -> {
                auditView?.generateReport(dataModels)
            }
        }
    }

    fun getDataBySterilizationOperator(userId: String, startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByCycleUser(userId, startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByCycleUser(userId, startDate, endDate))
                auditView?.generateReport(dataModels)
            }
            else -> {
                auditView?.generateReport(dataModels)
            }
        }
    }

    fun getDataByBITestOperator(userId: String, startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByBITestUser(userId, startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByBITestUser(userId, startDate, endDate))
                auditView?.generateReport(dataModels)
            }
            else -> {
                auditView?.generateReport(dataModels)
            }
        }
    }

    fun getDataStatus(status: String, startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        val temperaturePressureStatus = "%${status}%"
        when {
            dataDao.getDataByStatus(status, temperaturePressureStatus, startDate, endDate)
                .isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByStatus(status,
                    temperaturePressureStatus,
                    startDate,
                    endDate))
                auditView?.generateReport(dataModels)
            }
            else -> {
                auditView?.generateReport(dataModels)
            }
        }
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

    fun getUsers(): List<UsersModel>? {
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

    fun getAutoclaves(): List<AutoclaveModel>? {
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

    fun getManagerInitials(): String {
        return when {
            officesDao.getAllOffices().isNotEmpty() -> {
                officesDao.getAllOffices()[0].managerInitials
            }
            else -> {
                context?.getString(R.string.blank).toString()
            }
        }
    }
}