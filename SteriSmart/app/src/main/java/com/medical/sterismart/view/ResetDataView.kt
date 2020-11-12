package com.medical.sterismart.view

import com.medical.data.tables.DataModel

interface ResetDataView {
    fun showErrorMessage(message: String)
    fun deletedSuccess()
    fun getDataForDelete(dataModel: List<DataModel>)
}