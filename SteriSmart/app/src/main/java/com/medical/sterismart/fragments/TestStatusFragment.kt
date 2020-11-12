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
import com.medical.sterismart.presenter.AuditPresenter
import com.medical.sterismart.view.AuditView
import kotlinx.android.synthetic.main.fragment_audit_type.*
import kotlinx.android.synthetic.main.fragment_audit_type.container
import kotlinx.android.synthetic.main.fragment_test_fragment.*
import kotlinx.android.synthetic.main.fragment_test_fragment.get_report
import kotlinx.android.synthetic.main.fragment_test_fragment.initials
import java.lang.reflect.Type
import javax.inject.Inject

class TestStatusFragment : BaseFragment(), AuditView {

    var status = "Pass"
    var isPassed = true

    @Inject
    lateinit var auditPresenter: AuditPresenter

    companion object {
        fun getCallingFragment(): Fragment {
            return TestStatusFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        auditPresenter.initializeDb(activity!!)
        auditPresenter.auditView = this

        Thread {
            val managerInitials = auditPresenter.getManagerInitials()
            activity!!.runOnUiThread {
                initials.setText(managerInitials)
            }
        }.start()

        pass.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    status = "Pass"
                    isPassed = true
                }
            }
        }
        fail.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    status = "Fail"
                    isPassed = false
                }
            }
        }

        get_report.setOnClickListener {
            when {
                TextUtils.isEmpty(startDate) -> {
                    Snackbar.make(container, "Please select start date", Snackbar.LENGTH_SHORT)
                        .show()
                }
                TextUtils.isEmpty(endDate) -> {
                    Snackbar.make(container, "Please select end date", Snackbar.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(initials.text.toString()) -> {
                    Snackbar.make(container, "Please enter the initials", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Utility.hideKeyboard(get_report)
                    Thread {
                        auditPresenter.getDataStatus(status,
                            Utility.convertDate(startDate),
                            Utility.convertDate(endDate))
                    }.start()
                }
            }
        }
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(activity!!.application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {

    }

    override fun generateReport(reportList: List<DataModel>) {
        when {
            reportList.isNotEmpty() -> {
                activity!!.runOnUiThread {
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
                            packageDetail =
                                "$packageDetail${item.packageName}$packageType "
                        }
                        reportList[i].packageString = Gson().toJson(packageList)
                        reportList[i].packageInfo = packageDetail
                    }
                    navigator.loadReportFragment(activity!!,
                        reportList,
                        initials.text.toString(),
                        isAudit = true,
                        isFullAuditDetails = true,
                        isPassed = isPassed,isUniqueCode = false,uniqueCode = "")
                }
            }
            else -> {
                navigator.loadReportFragment(activity!!,
                    ArrayList(),
                    "",
                    isAudit = true,
                    isFullAuditDetails = true,
                    isPassed = isPassed,isUniqueCode = false,uniqueCode = "")
                Snackbar.make(container, "No Data Found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}