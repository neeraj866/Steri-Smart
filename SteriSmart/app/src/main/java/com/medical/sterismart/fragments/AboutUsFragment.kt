package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.medical.sterismart.R

class AboutUsFragment : BaseFragment() {

    companion object {
        fun getCallingFragment(): Fragment {
            return AboutUsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }
}