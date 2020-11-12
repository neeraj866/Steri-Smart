package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
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
import kotlinx.android.synthetic.main.dialog_ask_number.*
import kotlinx.android.synthetic.main.fragment_report_menu.*
import java.lang.reflect.Type
import javax.inject.Inject

class ReportMenuFragment : BaseFragment(), ReportView {


    @Inject
    lateinit var reportPresenter: ReportPresenter

    companion object {
        fun getCallingFragment(): Fragment {
            return ReportMenuFragment()
        }
    }

    var isUniqueCode = false
    var uniqueCode = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        reportPresenter.initializeDb(requireContext())
        reportPresenter.reportView = this

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        from_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    from_date.text = dateString
                }
            })
        }

        to_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    to_date.text = dateString
                }
            })
        }

        daily.setOnClickListener {
            when {
                TextUtils.isEmpty(from_date.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose the start date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Thread {
                        reportPresenter.getDataByDate(from_date.text.toString())
                    }.start()
                }
            }
        }
        date_range.setOnClickListener {
            when {
                TextUtils.isEmpty(from_date.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose the start date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(to_date.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose the end date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Thread {
                        reportPresenter.getDataByDateRange(
                            Utility.convertDate(from_date.text.toString()),
                            Utility.convertDate(to_date.text.toString())
                        )
                    }.start()
                }
            }
        }

        cycle_number.setOnClickListener {
            when {
                TextUtils.isEmpty(from_date.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose the start date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(to_date.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose the end date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    showNumberAskingDialog()
                }
            }
        }
    }

    private fun showNumberAskingDialog() {
        val dataDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = dataDialog.window?.attributes
        lp?.dimAmount = 0.5f

        dataDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dataDialog.setContentView(R.layout.dialog_ask_number)

        dataDialog.submit.setOnClickListener {
            when {
                TextUtils.isEmpty(dataDialog.dialog_cycle_number.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter the cycle number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    dataDialog.dismiss()
                    Thread {
                        reportPresenter.getDataByCycleNumber(dataDialog.dialog_cycle_number.text.toString())
                    }.start()
                }
            }
        }

        dataDialog.cancel.setOnClickListener {
            dataDialog.dismiss()
        }

        dataDialog.show()
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
                    }
                    navigator.loadReportFragment(
                        requireActivity(),
                        reportList,
                        "",
                        isAudit = false,
                        isFullAuditDetails = false,
                        isPassed = false, isUniqueCode = isUniqueCode, uniqueCode = uniqueCode
                    )
                }
            }
            else -> {
                Snackbar.make(container, "No Data Found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}