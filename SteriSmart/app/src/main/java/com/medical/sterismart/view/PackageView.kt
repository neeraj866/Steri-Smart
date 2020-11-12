package com.medical.sterismart.view

interface PackageView {
    fun showErrorMessage(message: String)
    fun addedSuccess()
    fun updateSuccess()
    fun deletedSuccess()
}