package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.OfficesDao
import com.medical.data.tables.OfficesModel
import com.medical.sterismart.view.OfficeView
import javax.inject.Inject

class OfficePresenter @Inject constructor() {

    var officeVIew: OfficeView? = null

    private var officeModel: OfficesModel? = null

    private lateinit var officesDao: OfficesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        officesDao = db.officesDao()
    }

    fun addOffice(officesModel: OfficesModel) {
        Thread {
            officesDao.addOffice(officesModel)
            officeVIew?.updateSuccess()
        }.start()
    }

    fun updateOffice(officesModel: OfficesModel) {
        Thread {
            officesDao.updateOffice(officesModel)
            officeVIew?.updateSuccess()
        }.start()
    }

    fun getOffice(): OfficesModel? {
        return when {
            officesDao.getAllOffices().isNotEmpty() -> {
                officeModel = officesDao.getAllOffices()[0]
                officeModel as OfficesModel
            }
            else -> {
                officeModel
            }
        }
    }


}