package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.DataDao
import com.medical.data.database.dao.OfficesDao
import com.medical.data.tables.DataModel
import com.medical.sterismart.R
import com.medical.sterismart.view.ReportView
import javax.inject.Inject

class ReportPresenter @Inject constructor() {

    var reportView: ReportView? = null


    private lateinit var dataDao: DataDao
    private lateinit var officeDao: OfficesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null


    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        officeDao = db.officesDao()
        dataDao = db.dataModelDao()
    }

    fun getDataByDate(date: String) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByDate(date).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByDate(date))
                reportView?.generateReport(dataModels)
            }
            else -> {
                reportView?.generateReport(dataModels)
            }
        }
    }

    fun getDataByDateRange(startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByDateRange(startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByDateRange(startDate, endDate))
                reportView?.generateReport(dataModels)
            }
            else -> {
                reportView?.generateReport(dataModels)
            }
        }
    }

    fun getDataByBarcode(barcode: String) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByBarcode(barcode).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByBarcode(barcode))
                reportView?.generateReport(dataModels)
            }
            else -> {
                reportView?.generateReport(dataModels)
            }
        }
    }

    fun getDataByCycleNumber(cycleNumber: String) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataByCycleNumber(cycleNumber).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataByCycleNumber(cycleNumber))
                reportView?.generateReport(dataModels)
            }
            else -> {
                reportView?.generateReport(dataModels)
            }
        }
    }


    fun getManagerInitials(): String {
        return when {
            officeDao.getAllOffices().isNotEmpty() -> {
                officeDao.getAllOffices()[0].managerInitials
            }
            else -> {
                context?.getString(R.string.blank).toString()
            }
        }
    }
}