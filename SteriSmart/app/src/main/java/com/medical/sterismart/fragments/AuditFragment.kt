package com.medical.sterismart.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.fragment_audit.*

class AuditFragment : BaseFragment() {

    companion object {
        fun getCallingFragment(): Fragment {
            return AuditFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_audit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initialiseDependency()
        makeAllButtonsGrey()
        package_text.setBackgroundColor(Color.TRANSPARENT)
        navigator.loadAuditTypeFragment(requireActivity(),
            "package",
            start_date.text.toString(),
            end_date.text.toString())

        package_text.setOnClickListener {
            makeAllButtonsGrey()
            package_text.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadAuditTypeFragment(requireActivity(),
                "package",
                start_date.text.toString(),
                end_date.text.toString())
        }

        autoclave.setOnClickListener {
            makeAllButtonsGrey()
            autoclave.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadAuditTypeFragment(requireActivity(),
                "autoclave",
                start_date.text.toString(),
                end_date.text.toString())
        }

        sterilization_operator.setOnClickListener {
            makeAllButtonsGrey()
            sterilization_operator.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.light_bg))
            navigator.loadAuditTypeFragment(requireActivity(),
                "sterilization_operator",
                start_date.text.toString(),
                end_date.text.toString())
        }

        biological_indicator_operator.setOnClickListener {
            makeAllButtonsGrey()
            biological_indicator_operator.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.light_bg))
            navigator.loadAuditTypeFragment(requireActivity(),
                "biological_indicator_operator",
                start_date.text.toString(),
                end_date.text.toString())
        }

        test_status.setOnClickListener {
            makeAllButtonsGrey()
            test_status.setBackgroundColor(Color.TRANSPARENT)
            navigator.loadTestReportFragment(requireActivity(),
                start_date.text.toString(),
                end_date.text.toString())
        }

        start_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    start_date.text = dateString
                    startDate = dateString
                }
            })
        }

        end_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    endDate = dateString
                    end_date.text = dateString
                }

            })
        }
    }

    fun goFurther(): Boolean {
        return when {
            TextUtils.isEmpty(start_date.text.toString()) -> {
                Snackbar.make(container, "Please select start date", Snackbar.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(end_date.text.toString()) -> {
                Snackbar.make(container, "Please select end date", Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun makeAllButtonsGrey() {
        val darkBg: Drawable =
            resources.getDrawable(R.drawable.rectangular_button_background_grey, null)
        package_text.background = darkBg
        autoclave.background = darkBg
        sterilization_operator.background = darkBg
        biological_indicator_operator.background = darkBg
        test_status.background = darkBg
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }
}