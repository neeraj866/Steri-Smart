package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.medical.data.tables.PackagesModel
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.InstrumentAdapter
import com.medical.sterismart.adapter.SterilizationPackageAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.model.DataPackageModel
import com.medical.sterismart.presenter.SterilizationPackagePresenter
import kotlinx.android.synthetic.main.dialog_add_new_package.*
import kotlinx.android.synthetic.main.fragment_sterilization_package.*
import javax.inject.Inject

class SterilizationPackageFragment : BaseFragment(),
    SterilizationPackageAdapter.OnPackageCountChangeListener {

    private var packageList: MutableList<PackagesModel> = ArrayList()

    @Inject
    lateinit var sterilizationPackagePresenter: SterilizationPackagePresenter
    private var sterilizationPackageAdapter: SterilizationPackageAdapter? = null

    companion object {
        fun getCallingFragment(): Fragment {
            return SterilizationPackageFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sterilization_package, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        sterilizationPackagePresenter.initializeDb(requireContext())
        setAdapter()
        getPackages()
        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        sterilizationPackagePresenter.deleteTable()
        generate_label.setOnClickListener {
            val sterilizationPackageList: MutableList<SterilizationPackagesModel> = ArrayList()
            Thread {
                for (items in packageList) {
                    sterilizationPackageList.clear()
                    var count = items.packageCount
                    while (count > 0) {

                        sterilizationPackagePresenter.addSterilizationPackages(
                            SterilizationPackagesModel(
                                System.currentTimeMillis(),
                                items.packageName,
                                items.packageType
                            )
                        )
                        count -= 1
                    }
                }
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Package Added", Toast.LENGTH_SHORT).show()
                    navigator.loadLabelFragment(requireActivity())
                }
            }.start()
        }

        add_new_package.setOnClickListener {
            showAddNewPackageDialog()
        }
    }

    private fun showAddNewPackageDialog() {

        val dataDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = dataDialog.window?.attributes
        lp?.dimAmount = 0.5f

        dataDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dataDialog.setContentView(R.layout.dialog_add_new_package)

        val selectedPackageItems: MutableList<DataPackageModel> = ArrayList()

        for (item in resources.getStringArray(R.array.instruments)) {
            selectedPackageItems.add(DataPackageModel(item, false))
        }

        val instrumentAdapter = InstrumentAdapter(requireActivity())
        instrumentAdapter.instrumentsItems = selectedPackageItems
        dataDialog.instrument_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        dataDialog.instrument_list.adapter = instrumentAdapter

        dataDialog.cancel_dialog.setOnClickListener {
            dataDialog.dismiss()
        }
        dataDialog.add_instrument.setOnClickListener {
            when {
                TextUtils.isEmpty(dataDialog.instrument_name.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter instrument name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    selectedPackageItems.add(
                        DataPackageModel(
                            dataDialog.instrument_name.text.toString(),
                            true
                        )
                    )
                    instrumentAdapter.notifyDataSetChanged()
                }
            }
        }
        dataDialog.add_package.setOnClickListener {
            when {
                TextUtils.isEmpty(dataDialog.package_name.text.toString()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter the package name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val instrumentList: MutableList<DataPackageModel> = ArrayList()
                    for (items in selectedPackageItems) {
                        when {
                            items.isSelected -> {
                                instrumentList.add(items)
                            }
                        }
                    }
                    packageList.add(
                        PackagesModel(
                            0,
                            Utility.convertJsonToString(instrumentList),
                            dataDialog.package_name.text.toString(),
                            false,
                            0,
                            "",
                            "Wrapped"
                        )
                    )
                    sterilizationPackageAdapter?.notifyDataSetChanged()
                    dataDialog.dismiss()
                }
            }
        }
        dataDialog.show()
    }

    private fun setAdapter() {
        sterilizationPackageAdapter = SterilizationPackageAdapter(requireActivity())
        package_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        sterilizationPackageAdapter?.packages = packageList
        sterilizationPackageAdapter?.onPackageCountChangeListener = this
        package_list.adapter = sterilizationPackageAdapter
    }

    private fun getPackages() {
        Thread {
            when {
                sterilizationPackagePresenter.getPackages() != null -> {
                    packageList.clear()
                    packageList.addAll(sterilizationPackagePresenter.getPackages()!!)
                    requireActivity().runOnUiThread {
                        sterilizationPackageAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }.start()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun packageCountChange(count: Int, position: Int) {
        packageList[position].packageCount = count
        sterilizationPackageAdapter?.notifyDataSetChanged()
    }

    override fun onTypeChanged(type: String, position: Int) {
        packageList[position].packageType = type
        sterilizationPackageAdapter?.notifyDataSetChanged()
    }
}