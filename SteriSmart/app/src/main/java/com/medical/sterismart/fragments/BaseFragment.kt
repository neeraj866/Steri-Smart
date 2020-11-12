package com.medical.sterismart.fragments

import androidx.fragment.app.Fragment
import com.medical.sterismart.Navigator
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var navigator: Navigator

    companion object {
        var startDate = ""
        var endDate = ""
    }
}