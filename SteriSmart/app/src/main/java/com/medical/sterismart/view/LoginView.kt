package com.medical.sterismart.view

interface LoginView {
    fun showErrorMessage(message: String)
    fun loginSuccess(isAdmin: Boolean)
}