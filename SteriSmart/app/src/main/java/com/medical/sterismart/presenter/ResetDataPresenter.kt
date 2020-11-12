package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.DataDao
import com.medical.data.database.dao.OfficesDao
import com.medical.data.tables.DataModel
import com.medical.sterismart.view.ResetDataView
import javax.inject.Inject

class ResetDataPresenter @Inject constructor() {

    var resetDataView: ResetDataView? = null

    private lateinit var dataDao: DataDao
    private lateinit var officesDao: OfficesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null


    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        dataDao = db.dataModelDao()
        officesDao = db.officesDao()
    }

    fun deleteAllDate(models: List<DataModel>) {
        Thread {
            for (items in models) {
                dataDao.deleteDate(items)
            }
            resetDataView?.deletedSuccess()
        }.start()
    }

    fun getDataExceptDates(startDate: Long, endDate: Long) {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getDataToDelete(startDate, endDate).isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getDataToDelete(startDate, endDate))
                resetDataView?.getDataForDelete(dataModels)
            }
            else -> {
                resetDataView?.getDataForDelete(dataModels)
            }
        }
    }

    fun getAllData() {
        val dataModels: MutableList<DataModel> = ArrayList()
        when {
            dataDao.getAllData().isNotEmpty() -> {
                dataModels.clear()
                dataModels.addAll(dataDao.getAllData())
                resetDataView?.getDataForDelete(dataModels)
            }
            else -> {
                resetDataView?.getDataForDelete(dataModels)
            }
        }
    }

    fun getOfficePin(): String {
        return when {
            officesDao.getAllOffices().isNotEmpty() -> {
                officesDao.getAllOffices()[0].pin
            }
            else -> {
                ""
            }
        }
    }
}