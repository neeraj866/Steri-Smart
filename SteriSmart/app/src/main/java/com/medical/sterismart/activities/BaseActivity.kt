package com.medical.sterismart.activities

import androidx.appcompat.app.AppCompatActivity
import com.medical.sterismart.Navigator
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(){

    @Inject
    lateinit var  navigator: Navigator
}