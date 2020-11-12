package com.medical.sterismart.view

import com.medical.data.tables.DataModel

interface AuditView {
    fun showErrorMessage(message: String)
    fun generateReport(reportList: List<DataModel>)
}