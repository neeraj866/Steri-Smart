package com.medical.sterismart.view

interface OperatorSetupView {
    fun showErrorMessage(message: String)
    fun addedSuccess()
    fun updateSuccess()
    fun deletedSuccess()
}