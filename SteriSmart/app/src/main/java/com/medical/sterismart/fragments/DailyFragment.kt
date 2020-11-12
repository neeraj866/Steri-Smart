package com.medical.sterismart.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medical.data.tables.DataModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.model.PackageModel
import com.medical.sterismart.presenter.ReportPresenter
import com.medical.sterismart.view.ReportView
import kotlinx.android.synthetic.main.fragment_daily.*
import java.lang.reflect.Type
import javax.inject.Inject

class DailyFragment : BaseFragment(), ReportView {

    @Inject
    lateinit var reportPresenter: ReportPresenter


    companion object {
        fun getCallingFragment(): Fragment {
            return DailyFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        reportPresenter.initializeDb(requireContext())
        reportPresenter.reportView = this

        Thread {
            val managerInitials = reportPresenter.getManagerInitials()
            requireActivity().runOnUiThread {
                initials.setText(managerInitials)
            }
        }.start()

        date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    date.text = dateString
                }
            })
        }

        generate_report.setOnClickListener {
            when {
                TextUtils.isEmpty(date.text.toString()) -> {
                    Snackbar.make(container, "Please select the date", Snackbar.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(initials.text.toString()) -> {
                    Snackbar.make(container, "Please enter the initials", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Utility.hideKeyboard(generate_report)
                    Thread {
                        reportPresenter.getDataByDate(date.text.toString())
                    }.start()
                }
            }
        }
        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {

    }

    override fun generateReport(reportList: List<DataModel>) {
        when {
            reportList.isNotEmpty() -> {
                requireActivity().runOnUiThread {
                    for (i in reportList.indices) {
                        val listType: Type = object : TypeToken<List<PackageModel?>?>() {}.type
                        val packageList: MutableList<PackageModel> =
                            Gson().fromJson(reportList[i].packageString, listType)
                        var packageDetail = ""
                        loop@ for (item in packageList) {
                            var packageType = ""
                            when {
                                !TextUtils.isEmpty(item.packageType) -> {
                                    packageType = "(${item.packageType})"
                                }
                            }
                            packageDetail = "$packageDetail${item.packageName} $packageType "
                        }
                        reportList[i].packageString = Gson().toJson(packageList)
                        reportList[i].packageInfo = packageDetail
                    }
                    navigator.loadReportFragment(
                        requireActivity(),
                        reportList,
                        initials.text.toString(),
                        isAudit = false,
                        isFullAuditDetails = false,
                        isPassed = false,isUniqueCode = false,uniqueCode = ""
                    )
                }
            }
            else -> {
                navigator.loadReportFragment(
                    requireActivity(),
                    ArrayList(),
                    "",
                    isAudit = false,
                    isFullAuditDetails = false,
                    isPassed = false,isUniqueCode = false,uniqueCode = ""
                )
                Snackbar.make(container, "No Data Found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}