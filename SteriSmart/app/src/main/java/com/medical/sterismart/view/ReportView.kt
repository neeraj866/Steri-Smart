package com.medical.sterismart.view

import com.medical.data.tables.DataModel

interface ReportView {
    fun showErrorMessage(message: String)
    fun generateReport(reportList: List<DataModel>)
}