package com.medical.sterismart.presenter

import android.content.Context
import android.text.TextUtils
import androidx.room.Room
import com.medical.data.database.dao.AppDatabase
import com.medical.data.database.dao.OfficesDao
import com.medical.data.tables.OfficesModel
import com.medical.sterismart.R
import com.medical.sterismart.view.LoginView
import javax.inject.Inject

class LoginPresenter @Inject constructor() {
    var loginView: LoginView? = null

    private lateinit var officesDao: OfficesDao
    private lateinit var db: AppDatabase
    private var context: Context? = null

    fun initializeDb(context: Context) {
        this.context = context
        db = Room.databaseBuilder(context, AppDatabase::class.java, "SteriSmart.db").fallbackToDestructiveMigration().build()
        officesDao = db.officesDao()
    }


    fun authenticateUser(pin: String) {
        Thread {
            when {
                TextUtils.isEmpty(pin) -> {
                    loginView?.showErrorMessage("Please enter the PIN")
                }
                officesDao.getAllOffices().isNotEmpty() -> {
                    val officeModel: OfficesModel = officesDao.getAllOffices()[0]
                    when {
                        TextUtils.equals(pin, officeModel.pin.toString()) -> {
                            loginView?.loginSuccess(false)
                        }
                        else -> {
                            loginView?.showErrorMessage("Wrong PIN")
                        }
                    }
                }
                TextUtils.equals(pin, "0000") -> {
                    loginView?.loginSuccess(true)
                }
                else -> {
                    loginView?.showErrorMessage("Wrong PIN")
                }
            }
        }.start()
    }

    fun getOffice(): String {
        return when {
            officesDao.getAllOffices().isNotEmpty() -> {
                officesDao.getAllOffices()[0].officeName
            }
            else -> {
                context?.getString(R.string.office_name).toString()
            }
        }
    }
}