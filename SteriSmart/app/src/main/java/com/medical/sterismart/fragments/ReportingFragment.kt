package com.medical.sterismart.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.medical.sterismart.R
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.fragment_reporting.*

class ReportingFragment : BaseFragment() {

    companion object {
        fun getCallingFragment(): Fragment {
            return ReportingFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reporting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initialiseDependency()
        navigator.loadDailyFragment(requireActivity())

        daily.setOnClickListener {
            makeAllButtonsGrey()
            daily.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_bg))
            navigator.loadDailyFragment(requireActivity())
        }
        date_range.setOnClickListener {
            makeAllButtonsGrey()
            date_range.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_bg
                )
            )
            navigator.loadDateRangeFragment(requireActivity())
        }
        barcode.setOnClickListener {
            makeAllButtonsGrey()
            barcode.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_bg))
            navigator.loadBarcodeFragment(requireActivity())
        }
    }

    private fun makeAllButtonsGrey() {
        val darkBg: Drawable =
            resources.getDrawable(R.drawable.rectangular_button_background_grey, null)
        daily.background = darkBg
        date_range.background = darkBg
        barcode.background = darkBg
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }
}