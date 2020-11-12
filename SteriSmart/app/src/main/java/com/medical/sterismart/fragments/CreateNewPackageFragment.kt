package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.InstrumentAdapter
import com.medical.sterismart.adapter.SelectedInstrumentAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.model.DataPackageModel
import com.medical.sterismart.presenter.PackagePresenter
import com.medical.sterismart.view.PackageView
import kotlinx.android.synthetic.main.dialog_data_list.*
import kotlinx.android.synthetic.main.fragment_create_new_package.*
import javax.inject.Inject

class CreateNewPackageFragment : BaseFragment(), PackageView,
    SelectedInstrumentAdapter.OnItemDeleteListener {
    private var allInstrumentAdapter: InstrumentAdapter? = null
    private var selectedInstrumentAdapter: SelectedInstrumentAdapter? = null
    private var allPackageItems: MutableList<DataPackageModel> = ArrayList()
    private var selectedPackageItems: MutableList<DataPackageModel> = ArrayList()
    private var packagesModel: PackagesModel? = null

    @Inject
    lateinit var packagePresenter: PackagePresenter
    private val instrumentList: MutableList<DataPackageModel> = ArrayList()

    private var onPackageChangeListener: OnPackageChangeListener? = null
    private var packageId = 0

    fun initializeListener(onPackageChangeListener: OnPackageChangeListener) {
        this.onPackageChangeListener = onPackageChangeListener
    }


    companion object {
        fun getCallingFragment(): Fragment {
            return CreateNewPackageFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_package, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAllInstrumentsData()
        setSelectedInstrumentsPackageAdapter()
        setSelectedInstrumentsData()
        initialiseDependency()

        packagePresenter.packageView = this
        packagePresenter.initializeDb(requireContext())
        instrumentList.clear()

        for (items in resources.getStringArray(R.array.instruments).toList()) {
            instrumentList.add(DataPackageModel(items, false))
        }

        packageId = requireArguments().getInt("package_id")
        when {
            packageId > 0 -> {
                getPackage()
            }
        }

        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }

        add_button.setOnClickListener {
            when {
                TextUtils.isEmpty(instrument_name.text.toString()) -> {

                }
                else -> {
                    Utility.hideKeyboard(add_button)
                    addNewInstrument()
                }
            }
        }

        save_button.setOnClickListener {
            when {
                goFurther() -> {
                    Utility.hideKeyboard(save_button)
                    when (packageId) {
                        0 -> {
                            savePackage()
                        }
                        else -> {
                            updatePackage()
                        }
                    }
                }
            }
        }
        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        add_instruments.setOnClickListener {
            showInstrumentsDialog()
        }
    }

    private fun getPackage() {
        Thread {
            when {
                packagePresenter.getPackage(packageId) != null -> {
                    packagesModel = packagePresenter.getPackage(packageId)
                    requireActivity().runOnUiThread {
                        package_name_edit_text.setText(packagesModel!!.packageName)
                        when {
                            !TextUtils.isEmpty(packagesModel!!.instruments) -> {
                                selectedPackageItems.addAll(
                                    Utility.convertStringToJson(
                                        packagesModel!!.instruments
                                    )
                                )
                                addItemsToSelectedList()
                            }
                        }
                    }
                }
            }
        }.start()
    }

    private var instrumentDialog: Dialog? = null
    private fun showInstrumentsDialog() {
        instrumentDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        instrumentDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = instrumentDialog?.window?.attributes
        lp?.dimAmount = 0.5f

        instrumentDialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        instrumentDialog?.setContentView(R.layout.dialog_data_list)

        for (items in selectedPackageItems) {
            loop@ for (item in instrumentList) {
                when (items.instrumentName) {
                    item.instrumentName -> {
                        instrumentList.remove(item)
                        break@loop
                    }
                }
            }
        }

        val instrumentAdapter = InstrumentAdapter(requireActivity())
        instrumentDialog?.data_list!!.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        instrumentList.sortBy { it.instrumentName }
        instrumentAdapter.instrumentsItems = instrumentList
        instrumentDialog?.data_list!!.adapter = instrumentAdapter

        instrumentDialog?.dialog_title!!.text = getString(R.string.select_instruments_to_add)

        instrumentDialog?.show()

        instrumentDialog?.cancel!!.setOnClickListener {
            instrumentDialog?.dismiss()
        }

        instrumentDialog?.add!!.visibility = View.VISIBLE
        instrumentDialog?.add!!.setOnClickListener {
            instrumentDialog?.dismiss()
            addItemsToSelectedList()
        }
        instrumentDialog?.show()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    private fun savePackage() {
        var instruments = ""
        when {
            selectedPackageItems.size > 0 -> {
                for (i in 0 until selectedPackageItems.size) {
                    selectedPackageItems[i].isSelected = false
                }
                instruments = Utility.convertJsonToString(selectedPackageItems)
            }
        }
        Thread {
            val packageModel = PackagesModel(
                packageId,
                instruments,
                package_name_edit_text.text.toString(),
                false,
                0,
                "",
                "Wrapped"
            )
            packagePresenter.addPackage(packageModel)
        }.start()
    }

    private fun updatePackage() {
        var instruments = ""
        when {
            selectedPackageItems.size > 0 -> {
                for (i in 0 until selectedPackageItems.size) {
                    selectedPackageItems[i].isSelected = false
                }
                instruments = Utility.convertJsonToString(selectedPackageItems)
            }
        }
        Thread {
            val updatePackageModel = PackagesModel(
                packageId,
                instruments,
                package_name_edit_text.text.toString(),
                false,
                0,
                "",
                packagesModel!!.packageType
            )
            packagePresenter.addPackage(updatePackageModel)
        }.start()
    }

    private fun goFurther(): Boolean {
        val packageName: String = package_name_edit_text.text.toString()
        return when {
            TextUtils.isEmpty(packageName) -> {
                Snackbar.make(container, "Please enter package name", Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun addItemsToSelectedList() {
        for (i in instrumentList.indices) {
            when {
                instrumentList[i].isSelected -> {
                    selectedPackageItems.add(instrumentList[i])
                }
            }
        }

        allInstrumentAdapter?.notifyDataSetChanged()
        selectedPackageItems.sortBy { it.instrumentName }
        selectedInstrumentAdapter?.notifyDataSetChanged()
    }

    private fun addNewInstrument() {
        if (TextUtils.isEmpty(instrument_name.text.toString())) {
            Snackbar.make(container, "Enter the instrument name", Snackbar.LENGTH_SHORT).show()
            return
        }
        selectedPackageItems.add(DataPackageModel(instrument_name.text.toString(), true))
        selectedPackageItems.sortBy { it.instrumentName }
        selectedInstrumentAdapter?.notifyDataSetChanged()
        instrument_name.setText(getString(R.string.blank))
    }

    private fun setAllInstrumentsData() {
        var count = 0
        resources.getStringArray(R.array.instruments).forEach { _ ->
            val packageItem =
                DataPackageModel(resources.getStringArray(R.array.instruments)[count], false)
            allPackageItems.add(packageItem)
            count++
        }
        allInstrumentAdapter?.instrumentsItems = allPackageItems
        allInstrumentAdapter?.notifyDataSetChanged()
    }

    private fun setSelectedInstrumentsData() {
        selectedInstrumentAdapter?.instrumentsItems = selectedPackageItems
        selectedInstrumentAdapter?.onItemDeleteListener = this
        selectedInstrumentAdapter?.notifyDataSetChanged()
    }

    private fun setSelectedInstrumentsPackageAdapter() {
        selectedInstrumentAdapter = SelectedInstrumentAdapter(requireActivity())
        selected_instruments_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        selected_instruments_list.adapter = selectedInstrumentAdapter
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(container, "Something went wrong", Snackbar.LENGTH_SHORT).show()
    }

    override fun addedSuccess() {
        Snackbar.make(container, "Package created successfully", Snackbar.LENGTH_SHORT).show()
        requireActivity().runOnUiThread {
            onPackageChangeListener?.onPackageChanged()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun updateSuccess() {
        Snackbar.make(container, "Package created successfully", Snackbar.LENGTH_SHORT).show()
        requireActivity().runOnUiThread {
            onPackageChangeListener?.onPackageChanged()
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    override fun deletedSuccess() {

    }

    override fun onItemDelete(item: DataPackageModel) {
        selectedPackageItems.remove(item)
        item.isSelected = false
        instrumentList.add(item)
        selectedPackageItems.sortBy { it.instrumentName }
        selectedInstrumentAdapter?.notifyDataSetChanged()
    }

    interface OnPackageChangeListener {
        fun onPackageChanged()
    }
}