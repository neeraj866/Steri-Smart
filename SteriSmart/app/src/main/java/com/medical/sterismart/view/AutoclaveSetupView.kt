package com.medical.sterismart.view

interface AutoclaveSetupView {
    fun showErrorMessage(message: String)
    fun addedSuccess()
    fun updateSuccess()
    fun deletedSuccess()
}