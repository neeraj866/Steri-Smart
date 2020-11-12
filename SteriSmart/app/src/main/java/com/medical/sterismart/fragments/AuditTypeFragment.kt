package com.medical.sterismart.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import java.lang.reflect.Type
import javax.inject.Inject

class AuditTypeFragment : BaseFragment(), AuditView {

    @Inject
    lateinit var auditPresenter: AuditPresenter

    var type: String = ""
    var selectedValue = ""
    var userId = ""

    companion object {
        fun getCallingFragment(): Fragment {
            return AuditTypeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audit_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        auditPresenter.initializeDb(requireActivity())
        auditPresenter.auditView = this

        type = arguments?.getString("type").toString()


        Thread {
            val managerInitials = auditPresenter.getManagerInitials()
            requireActivity().runOnUiThread {
                initials.setText(managerInitials)
            }
        }.start()

        changeTheType()
        get_report.setOnClickListener {
            when {
                TextUtils.isEmpty(startDate) -> {
                    Snackbar.make(container, "Please select start date", Snackbar.LENGTH_SHORT)
                        .show()
                }
                TextUtils.isEmpty(endDate) -> {
                    Snackbar.make(container, "Please select end date", Snackbar.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(selectedValue) -> {
                    Snackbar.make(container, "Nothing is selected", Snackbar.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(initials.text.toString()) -> {
                    Snackbar.make(container, "Please enter the initials", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Utility.hideKeyboard(get_report)
                    when (type) {
                        "package" -> {
                            Thread {
                                auditPresenter.getDataByPackage(
                                    selectedValue,
                                    Utility.convertDate(startDate),
                                    Utility.convertDate(endDate)
                                )
                            }.start()
                        }
                        "autoclave" -> {
                            Thread {
                                auditPresenter.getDataByAutoclave(
                                    selectedValue,
                                    Utility.convertDate(startDate),
                                    Utility.convertDate(endDate)
                                )
                            }.start()
                        }
                        "sterilization_operator" -> {
                            when {
                                !TextUtils.isEmpty(userId) -> {
                                    Thread {
                                        auditPresenter.getDataBySterilizationOperator(
                                            userId,
                                            Utility.convertDate(startDate),
                                            Utility.convertDate(endDate)
                                        )
                                    }.start()
                                }
                            }
                        }
                        "biological_indicator_operator" -> {
                            when {
                                !TextUtils.isEmpty(userId) -> {
                                    Thread {
                                        auditPresenter.getDataByBITestOperator(
                                            userId,
                                            Utility.convertDate(startDate),
                                            Utility.convertDate(endDate)
                                        )
                                    }.start()
                                }
                            }
                        }
                    }
                }
            }
        }

        package_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedValue = when {
                    position > 0 -> {
                        when (type) {
                            "sterilization_operator" -> {
                                Thread {
                                    when {
                                        auditPresenter.getUsers() != null -> {
                                            userId =
                                                auditPresenter.getUsers()!![position - 1].id.toString()
                                        }
                                    }

                                }.start()
                            }
                            "biological_indicator_operator" -> {
                                Thread {
                                    when {
                                        auditPresenter.getUsers() != null -> {
                                            userId =
                                                auditPresenter.getUsers()!![position - 1].id.toString()
                                        }
                                    }

                                }.start()
                            }
                        }
                        package_spinner.selectedItem.toString()
                    }
                    else -> {
                        ""
                    }
                }
            }

        }
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    private fun changeTheType() {
        when (type) {
            "package" -> {
                select_package.text = getString(R.string.select_package)
                getPackages()
            }
            "autoclave" -> {
                select_package.text = getString(R.string.select_autoclave)
                getAutoclave()
            }
            "sterilization_operator" -> {
                select_package.text = getString(R.string.select_sterilization_operator)
                getSterilizationUsers()
            }
            "biological_indicator_operator" -> {
                select_package.text = getString(R.string.select_b_i_test_operator)
                getSterilizationUsers()
            }
        }
    }

    private fun getPackages() {
        val packages: MutableList<String> = ArrayList()
        packages.add("Select Package")
        val adapter = ArrayAdapter(context as Context, R.layout.spiiner_dropdown, packages)
        package_spinner.adapter = adapter
        Thread {
            when {
                auditPresenter.getPackages() != null -> {
                    for (item in auditPresenter.getPackages()!!) {
                        packages.add(item.packageName)
                    }
                    requireActivity().runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }.start()
    }

    private fun getAutoclave() {
        val autoclave: MutableList<String> = ArrayList()
        autoclave.add("Select Autoclave")
        val adapter = ArrayAdapter(context as Context, R.layout.spiiner_dropdown, autoclave)
        package_spinner.adapter = adapter
        Thread {
            when {
                auditPresenter.getAutoclaves() != null -> {
                    for (item in auditPresenter.getAutoclaves()!!) {
                        autoclave.add(item.name)
                    }
                    requireActivity().runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }.start()
    }

    private fun getSterilizationUsers() {
        val users: MutableList<String> = ArrayList()
        users.add("Select Operator")
        val adapter = ArrayAdapter(context as Context, R.layout.spiiner_dropdown, users)
        package_spinner.adapter = adapter
        Thread {
            when {
                auditPresenter.getUsers() != null -> {
                    for (item in auditPresenter.getUsers()!!) {
                        users.add("${item.firstName} ${item.lastName}")
                    }
                    requireActivity().runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }.start()
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
                            packageDetail =
                                "$packageDetail${item.packageName}$packageType "
                        }
                        reportList[i].packageString = Gson().toJson(packageList)
                        reportList[i].packageInfo = packageDetail
                    }
                    navigator.loadReportFragment(
                        requireActivity(),
                        reportList,
                        initials.text.toString(),
                        isAudit = true,
                        isFullAuditDetails = false,
                        isPassed = false, isUniqueCode = false, uniqueCode = ""
                    )
                }
            }
            else -> {
                navigator.loadReportFragment(
                    requireActivity(),
                    ArrayList(),
                    "",
                    isAudit = true,
                    isFullAuditDetails = false,
                    isPassed = false, isUniqueCode = false, uniqueCode = ""
                )
                Snackbar.make(container, "No Data Found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}