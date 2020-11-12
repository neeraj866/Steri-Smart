package com.medical.sterismart.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.medical.sterismart.R
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.fragment_help_center.*

class HelpCenterFragment : BaseFragment() {

    companion object {
        fun getCallingFragment(): Fragment {
            return HelpCenterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_help_center, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        navigator.loadHelpFragment(activity as FragmentActivity)
        help.setOnClickListener {
            makeAllButtonsGrey()
            help.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadHelpFragment(activity as FragmentActivity)
        }
        training.setOnClickListener {
            makeAllButtonsGrey()
            training.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadTrainingFragment(activity as FragmentActivity)
        }
        documentation.setOnClickListener {
            makeAllButtonsGrey()
            documentation.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadDocumentationFragment(activity as FragmentActivity)
        }
        manual_button.setOnClickListener {
            makeAllButtonsGrey()
            manual_button.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadManualFragment(activity as FragmentActivity)
        }
    }

    private fun makeAllButtonsGrey() {
        val darkBg: Drawable =
            resources.getDrawable(R.drawable.rectangular_button_background_grey, null)
        help.background = darkBg
        training.background = darkBg
        documentation.background = darkBg
        manual_button.background = darkBg
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(activity!!.application)).build()
        applicationComponent.inject(this)
    }
}