package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.PackagesDao
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.view.PackageView
import javax.inject.Inject

class PackagePresenter @Inject constructor() {

    var packageView: PackageView? = null

    private var packagesModels: MutableList<PackagesModel>? = null

    private lateinit var packagesDao: PackagesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null
    private var packagesModel: PackagesModel? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        packagesDao = db.packagesDao()
    }

    fun addPackage(packagesModel: PackagesModel) {
        Thread {
            packagesDao.addPackage(packagesModel)
            packageView?.addedSuccess()
        }.start()
    }


    fun getPackage(id: Int): PackagesModel? {
        return when {
            packagesDao.getPackageById(id) != null -> {
                packagesModel = packagesDao.getPackageById(id)
                packagesModel as PackagesModel
            }
            else -> {
                packagesModel
            }
        }
    }

    fun getPackageNextId(): Int? {
        var nextId = 1
        when {
            packagesDao.getAllPackages().isNotEmpty() -> {
                packagesModels = ArrayList()
                packagesModels?.addAll(packagesDao.getAllPackages())
                nextId =
                    (packagesModels as ArrayList<PackagesModel>)[(packagesModels as ArrayList<PackagesModel>).size - 1].id + 1
            }
        }
        return nextId
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

    fun updatePackage(packageModel: PackagesModel) {
        Thread {
            packagesDao.updatePackage(packageModel)
            packageView?.updateSuccess()
        }.start()
    }

    fun deletePackage(packageModel: PackagesModel) {
        Thread {
            packagesDao.deletePackage(packageModel)
            packageView?.deletedSuccess()
        }.start()
    }
}