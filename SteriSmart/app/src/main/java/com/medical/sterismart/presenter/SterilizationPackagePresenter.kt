package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.PackagesDao
import com.medical.data.database.dao.SterilizationPackagesDao
import com.medical.data.tables.PackagesModel
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.view.SterilizationPackageView
import javax.inject.Inject

class SterilizationPackagePresenter @Inject constructor() {
    private var packagesModels: MutableList<PackagesModel>? = null
    private lateinit var packagesDao: PackagesDao
    private lateinit var sterilizationPackagesDao: SterilizationPackagesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null
    var sterilizationPackageView: SterilizationPackageView? = null


    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        packagesDao = db.packagesDao()
        sterilizationPackagesDao = db.sterilizationPackagesDao()
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

    fun deleteTable() {
        Thread {
            sterilizationPackagesDao.nukeTable()
        }.start()
    }

    fun addSterilizationPackages(item: SterilizationPackagesModel) {
        sterilizationPackagesDao.addSterilizationPackage(item)
    }

    private var sterilizationPackagesModelList: MutableList<SterilizationPackagesModel>? = null

    fun getSterilizationPackages(): MutableList<SterilizationPackagesModel>? {

        return when {
            sterilizationPackagesDao.getAllSterilizationPackages().isNotEmpty() -> {
                when {
                    sterilizationPackagesModelList != null -> {
                        sterilizationPackagesModelList?.clear()
                    }
                    else -> {
                        sterilizationPackagesModelList = ArrayList()
                    }
                }
                sterilizationPackagesModelList?.addAll(sterilizationPackagesDao.getAllSterilizationPackages())
                sterilizationPackagesModelList
            }
            else -> {
                sterilizationPackagesModelList
            }
        }
    }

}