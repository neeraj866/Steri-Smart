package com.medical.sterismart.presenter

import android.content.Context
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.AutoclaveDao
import com.medical.data.tables.AutoclaveModel
import com.medical.sterismart.view.AutoclaveSetupView
import javax.inject.Inject

class AutoclaveSetupPresenter @Inject constructor() {

    var autoclaveSetupView: AutoclaveSetupView? = null

    private var autoclaveModels: MutableList<AutoclaveModel>? = null

    private lateinit var autoclaveDao: AutoclaveDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db")
            .fallbackToDestructiveMigration().build()
        autoclaveDao = db.autoclaveDao()
    }

    fun addAutoclave(autoclaveModel: AutoclaveModel) {
        Thread {
            autoclaveDao.addAutoclave(autoclaveModel)
            autoclaveSetupView?.addedSuccess()
        }.start()
    }

    fun updateAutoclave(autoclaveModel: AutoclaveModel) {
        Thread {
            autoclaveDao.updateAutoclave(autoclaveModel)
            autoclaveSetupView?.updateSuccess()
        }.start()
    }

    fun getAutoclavesNextId(): Int? {
        var nextId = 1
        when {
            autoclaveDao.getAllAutoclaves().isNotEmpty() -> {
                autoclaveModels = ArrayList()
                autoclaveModels?.addAll(autoclaveDao.getAllAutoclaves())
                nextId =
                    (autoclaveModels as ArrayList<AutoclaveModel>)[(autoclaveModels as ArrayList<AutoclaveModel>).size - 1].id + 1
            }
        }
        return nextId
    }

    fun deleteAutoclave(autoclaveModel: AutoclaveModel) {
        Thread {
            autoclaveDao.deleteAutoclave(autoclaveModel)
            autoclaveSetupView?.deletedSuccess()
        }.start()
    }

    fun getAutoclaves(): MutableList<AutoclaveModel>? {

        return when {
            autoclaveDao.getAllAutoclaves().isNotEmpty() -> {
                when {
                    autoclaveModels != null -> {
                        autoclaveModels?.clear()
                    }
                    else -> {
                        autoclaveModels = ArrayList()
                    }
                }
                autoclaveModels?.addAll(autoclaveDao.getAllAutoclaves())
                autoclaveModels
            }
            else -> {
                when {
                    autoclaveModels != null -> {
                        autoclaveModels?.clear()
                    }
                }
                autoclaveModels
            }
        }
    }

    fun getAutoclave(id: Int): AutoclaveModel {
        return autoclaveDao.getAutoclaveById(id)
    }
}