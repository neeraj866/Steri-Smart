package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.OfficesDao
import com.medical.data.database.dao.UsersDao
import com.medical.data.tables.OfficesModel
import com.medical.sterismart.view.GenerateReportView
import javax.inject.Inject

class GenerateReportPresenter @Inject constructor() {
    var generateReportView: GenerateReportView? = null

    private lateinit var officesDao: OfficesDao
    private lateinit var usersDao: UsersDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        officesDao = db.officesDao()
        usersDao = db.usersDao()
    }

    fun getOffice() {
        var officesModel: OfficesModel?
        when {
            officesDao.getAllOffices().isNotEmpty() -> {
                officesModel = officesDao.getAllOffices()[0]
                generateReportView?.officeDetails(officesModel)
            }
        }
    }

    fun getUserSignature(id: Int): String {
        return usersDao.getUserById(id).signature
    }
}