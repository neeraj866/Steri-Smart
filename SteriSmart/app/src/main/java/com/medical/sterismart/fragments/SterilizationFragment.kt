package com.medical.sterismart.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.medical.data.tables.AutoclaveModel
import com.medical.data.tables.DataModel
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.data.tables.UsersModel
import com.medical.sterismart.R
import com.medical.sterismart.TimePickerFragment
import com.medical.sterismart.adapter.AutoclaveModelAdapter
import com.medical.sterismart.adapter.BarcodeAdapter
import com.medical.sterismart.custom.DialogUtils
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.model.PackageModel
import com.medical.sterismart.presenter.SterilizationPresenter
import com.medical.sterismart.view.SterilizationView
import kotlinx.android.synthetic.main.dialog_data_list.*
import kotlinx.android.synthetic.main.fragment_sterilization.*
import javax.inject.Inject
import kotlin.math.roundToInt

class SterilizationFragment : BaseFragment(), TimePickerFragment.OnTimeChangeListener,
    SterilizationView {

    private var barcodeAdapter: BarcodeAdapter? = null

    private val packageItems: MutableList<PackageModel> = ArrayList()

    private var dataModel: DataModel? = null

    @Inject
    lateinit var sterilizationPresenter: SterilizationPresenter

    private var autoclaveManufacturer = ""
    private var autoclaveModelText = ""
    private var selectedAutoclaveModel: AutoclaveModel? = null

    private var operator = ""
    private var operatorName = ""
    private var chemicalIndicator = "Fail"
    private var maximumTemperature = "Fail"
    private var maximumPressure = "Fail"

    private var bowieDickTest = "N/A"

    private var maxTemp = 0.0
    private var maxPressure = 0.0
    private var minTemp = 0.0
    private var minPressure = 0.0

    private var temperatureUnit = "C"
    private var pressureUnit = "PSI"
    private var operatorsList: MutableList<UsersModel>? = null
    private var autoclaveModelList: MutableList<AutoclaveModel>? = null
    private var isCyclePass = false
    private var cycleResult = "Pass"

    companion object {
        fun getCallingFragment(): Fragment {
            return SterilizationFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sterilization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        sterilizationPresenter.initializeDb(requireContext())
        sterilizationPresenter.sterilizationView = this

        getSterilizationPackages()
        getOperators()
        getAutoclave()
        date.text = Utility.getCurrentDate()
        time.text = Utility.getCurrentTime()
        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }

        date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    date.text = dateString
                }
            })
        }

        time.setOnClickListener {
            val timePickerDialog = TimePickerFragment()
            timePickerDialog.initializeListener(this)
            timePickerDialog.show(requireActivity().supportFragmentManager, "")
        }

        operators.setOnClickListener {
            when {
                operatorsList != null && operatorsList?.size!! > 0 -> {
                    showOperatorDialog()
                }
                else -> {
                    Toast.makeText(requireContext(), "Add operators first", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        cycle_pass.setOnClickListener {
            when {
                furtherGo() -> {
                    Utility.hideKeyboard(cycle_pass)
                    isCyclePass = true
                    cycleResult = "Pass"
                    addSterilizationData()
                }
            }
        }

        cycle_fail.setOnClickListener {
            when {
                furtherGo() -> {
                    Utility.hideKeyboard(cycle_pass)
                    isCyclePass = false
                    cycleResult = "Fail"
                    addSterilizationData()
                }
            }
        }

        autoclaves.setOnClickListener {
            when {
                autoclaveModelList != null && autoclaveModelList?.size!! > 0 -> {
                    showAutoclaveDialog()
                }
                else -> {
                    Toast.makeText(requireContext(), "No Autoclave Added", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        celcius.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    temperatureUnit = "C"
                    maxTemp = 190.0
                    minTemp = 121.0
                    checkTemperature()
                }
            }
        }
        farenheit.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    temperatureUnit = "F"
                    maxTemp = 375.0
                    minTemp = 250.0
                    checkTemperature()
                }
            }
        }

        psi.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    pressureUnit = "PSI"
                    maxPressure = 40.0
                    minPressure = 15.0
                    checkPressure()
                }
            }
        }
        kpa.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    pressureUnit = "KPA"
                    maxPressure = 276.0
                    minPressure = 103.0
                    checkPressure()
                }
            }
        }
        maximum_temperature.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkTemperature()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        maximum_pressure.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkPressure()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun checkPressure() {
        when {
            TextUtils.isEmpty(maximum_pressure.text.toString()) -> {
                maximum_pressure.setTextColor(Color.BLACK)
                pressure_range.visibility = View.GONE
            }
            maximum_pressure.text.toString()
                .toDouble() > maxPressure || maximum_pressure.text.toString()
                .toDouble() < minPressure -> {
                pressure_range.visibility = View.VISIBLE
                scroll_view.post {
                    scroll_view.fullScroll(View.FOCUS_DOWN)
                    maximum_pressure.requestFocus()
                }

            }
            else -> {
                pressure_range.visibility = View.GONE
            }
        }
    }

    private fun checkTemperature() {
        when {
            TextUtils.isEmpty(maximum_temperature.text.toString()) -> {
                maximum_temperature.setTextColor(Color.BLACK)
                temp_range.visibility = View.GONE
            }
            maximum_temperature.text.toString()
                .toDouble() > maxTemp || maximum_temperature.text.toString()
                .toDouble() < minTemp -> {
                temp_range.visibility = View.VISIBLE
                scroll_view.post {
                    scroll_view.fullScroll(View.FOCUS_DOWN)
                    maximum_temperature.requestFocus()
                }
            }
            else -> {
                temp_range.visibility = View.GONE
            }
        }
    }

    private fun showAutoclaveDialog() {
        val dataDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = dataDialog.window?.attributes
        lp?.dimAmount = 0.5f

        dataDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dataDialog.setContentView(R.layout.dialog_data_list)

        dataDialog.dialog_title.text = getString(R.string.autoclave)

        val modelAdapter = AutoclaveModelAdapter(requireActivity())
        dataDialog.data_list!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        modelAdapter.autoclaveModel = autoclaveModelList!!
        dataDialog.data_list!!.adapter = modelAdapter
        modelAdapter.onModelSelected = object : AutoclaveModelAdapter.OnModelSelected {
            override fun selectedManufacturer(autoclaveModel: AutoclaveModel) {
                autoclaveManufacturer = autoclaveModel.manufacturer
                autoclaveModelText = autoclaveModel.model
                selectedAutoclaveModel = autoclaveModel
                autoclaves.text = selectedAutoclaveModel!!.name
                setAutoclaveData()
                dataDialog.dismiss()
            }

        }
        dataDialog.cancel.setOnClickListener { dataDialog.dismiss() }

        dataDialog.show()

    }

    private fun setAutoclaveData() {
        val cycleNumber = selectedAutoclaveModel?.cycleNumber!!.toInt() + 1

        when (selectedAutoclaveModel?.temperature_unit) {
            "C" -> {
                temperatureUnit = "C"
                maxTemp = 190.0
                minTemp = 121.0
                celcius.isChecked = true
            }
            else -> {
                temperatureUnit = "F"
                maxTemp = 375.0
                minTemp = 250.0
                farenheit.isChecked = true
            }
        }

        when (selectedAutoclaveModel?.pressure_unit) {
            "PSI" -> {
                pressureUnit = "PSI"
                maxPressure = 40.0
                minPressure = 15.0
                psi.isChecked = true
            }
            else -> {
                pressureUnit = "KPA"
                maxPressure = 276.0
                minPressure = 103.0
                kpa.isChecked = true
            }
        }

        cycle_number.setText(cycleNumber.toString())

        sterilization_duration.setText(selectedAutoclaveModel?.duration)
        maximum_temperature.setText(selectedAutoclaveModel?.maximum_temperature)
        maximum_pressure.setText(selectedAutoclaveModel?.maximum_pressure)
    }

    private fun getAutoclave() {
        Thread {
            when {
                sterilizationPresenter.getAutoclaves() != null -> {
                    autoclaveModelList = sterilizationPresenter.getAutoclaves()
                }
            }
        }.start()
    }

    private fun getOperators() {
        Thread {
            when {
                sterilizationPresenter.getUsers() != null -> {
                    operatorsList = sterilizationPresenter.getUsers()
                }
            }
        }.start()
    }

    private fun showOperatorDialog() {
        val operatorsTempList: MutableList<String> = ArrayList()
        for (items in operatorsList!!) {
            operatorsTempList.add("${items.firstName} ${items.lastName}")
        }
        DialogUtils.showListDialog(
            requireContext(),
            "Operators",
            operatorsTempList,
            object : DialogUtils.OnItemSelected {
                override fun selectedItem(item: String) {
                    loop@ for (items in operatorsList!!) {
                        when ("${items.firstName} ${items.lastName}") {
                            item -> {
                                operator = items.id.toString()
                                break@loop
                            }
                        }
                    }
                    operators.text = item
                    operatorName = item
                }

            })
    }

    private var sterilizationPackageList: MutableList<SterilizationPackagesModel> = ArrayList()

    private fun getSterilizationPackages() {
        Thread {
            when {
                sterilizationPresenter.getSterilizationPackages() != null -> {
                    sterilizationPackageList.addAll(sterilizationPresenter.getSterilizationPackages()!!)
                    val packageSize = sterilizationPackageList.size
                    requireActivity().runOnUiThread {
                        when {
                            packageSize > 0 -> {
                                cycle_pass.isEnabled = true
                                cycle_fail.isEnabled = true
                            }
                            else -> {
                                cycle_pass.isEnabled = false
                                cycle_fail.isEnabled = false
                            }
                        }
                        val packageString = "$packageSize Packages"
                        package_count.text = packageString
                    }

                }
            }
        }.start()
    }


    private fun addSterilizationData() {
        Thread {
            val id = sterilizationPresenter.getDataNextId()
            val packages: MutableList<PackageModel> = ArrayList()
            when {
                packageItems.size > 0 -> {
                    packages.addAll(packageItems)
                }
            }
            val packagesString = Gson().toJson(packages)
            var biResult = "Pending"
            var biLotNumber = ""
            var biUserId = ""
            var biTestOperator = ""

            maximumTemperature = "Pass"
            maximumPressure = "Pass"
            chemicalIndicator = "Pass"
            when {
                maximum_temperature.text.toString()
                    .toDouble() > maxTemp || maximum_temperature.text.toString()
                    .toDouble() < minTemp -> {
                    maximumTemperature = "Fail"
                }
            }
            when {
                maximum_pressure.text.toString()
                    .toDouble() > maxPressure || maximum_pressure.text.toString()
                    .toDouble() < minPressure -> {
                    maximumPressure = "Fail"
                }
            }

            when {
                sterilizationPresenter.getDataByPass(
                    date.text.toString(),
                    selectedAutoclaveModel?.name!!
                ) != null -> {
                    biResult = sterilizationPresenter.getDataByPass(
                        date.text.toString(),
                        selectedAutoclaveModel?.name!!
                    )!!.biResult
                    biLotNumber =
                        sterilizationPresenter.getDataByPass(
                            date.text.toString(),
                            selectedAutoclaveModel?.name!!
                        )!!.biLotNumber
                    biUserId = sterilizationPresenter.getDataByPass(
                        date.text.toString(),
                        selectedAutoclaveModel?.name!!
                    )!!.biUserId
                    biTestOperator =
                        sterilizationPresenter.getDataByPass(
                            date.text.toString(),
                            selectedAutoclaveModel?.name!!
                        )!!.biUsername
                }
            }
            var biTestDate = ""
            var biTestTime = ""
            var type1Result = ""
            var type4Result = ""
            var type5Result = ""
            when {
                sterilizationPresenter.getDataByDate(
                    date.text.toString(),
                    selectedAutoclaveModel!!.name
                ).size > 0 -> {
                    for (item in sterilizationPresenter.getDataByDate(
                        date.text.toString(),
                        selectedAutoclaveModel!!.name
                    )) {
                        when {
                            !TextUtils.isEmpty(item.biTestDate) -> {
                                biTestDate = item.biTestDate
                                type1Result = item.typeOneStatus
                                type4Result = item.typeFourStatus
                                type5Result = item.typeFiveStatus
                            }
                        }
                        when {
                            !TextUtils.isEmpty(item.biTestTime) -> {
                                biTestTime = item.biTestTime
                            }
                        }
                    }
                }
            }
            var duration = ""
            duration = when {
                sterilization_duration.text.contains(".") -> {
                    "${sterilization_duration.text.toString().toDouble().roundToInt()}"
                }
                else -> {
                    sterilization_duration.text.toString()
                }
            }
            val packagesData = Gson().toJson(sterilizationPackageList)
            dataModel = DataModel(
                id!!,
                biLotNumber,
                biResult,
                "",
                biUserId,
                biTestOperator,
                biTestDate,
                biTestTime,
                bowieDickTest,
                chemicalIndicator,
                "",
                selectedAutoclaveModel!!.name,
                autoclaveManufacturer,
                autoclaveModelText,
                date.text.toString(),
                Utility.convertDate(date.text.toString()),
                "$chemicalIndicator-${ci_lot.text}",
                cycle_number.text.toString(),
                "${maximum_pressure.text} $pressureUnit", maximumPressure,
                "${maximum_temperature.text} $temperatureUnit", maximumTemperature,
                time.text.toString(),
                operator,
                operatorName,
                packagesString,
                "$duration Minutes",
                type5Result,
                type4Result,
                type1Result,
                cycleResult,
                packagesData
            )
            sterilizationPresenter.addData(dataModel!!)
            sterilizationPresenter.deleteTable()
            when {
                isCyclePass -> {
                    when {
                        selectedAutoclaveModel != null -> {
                            selectedAutoclaveModel!!.cycleNumber = cycle_number.text.toString()
                            sterilizationPresenter.updateAutoclave(selectedAutoclaveModel!!)
                        }
                    }
                }
            }
        }.start()
    }

    private fun furtherGo(): Boolean {
        when {
            operators.text == "Select Operator" -> {
                Snackbar.make(container, "Please select operator", Snackbar.LENGTH_SHORT).show()
                return false
            }
            autoclaves.text == "Select Autoclave" -> {
                Snackbar.make(container, "Please select autoclave", Snackbar.LENGTH_SHORT).show()
                return false
            }
            TextUtils.isEmpty(cycle_number.text.toString()) -> {
                Snackbar.make(container, "Please Enter cycle number", Snackbar.LENGTH_SHORT).show()
                return false
            }
            TextUtils.isEmpty(date.text.toString()) -> {
                Snackbar.make(container, "Please select sterilization date", Snackbar.LENGTH_SHORT)
                    .show()
                return false
            }
            TextUtils.isEmpty(time.text.toString()) -> {
                Snackbar.make(container, "Please select sterilization time", Snackbar.LENGTH_SHORT)
                    .show()
                return false
            }
            TextUtils.isEmpty(sterilization_duration.text.toString()) -> {
                Snackbar.make(
                    container,
                    "Please enter sterilization duration",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }
            TextUtils.isEmpty(maximum_temperature.text.toString()) -> {
                Snackbar.make(container, "Please enter the temperature", Snackbar.LENGTH_SHORT)
                    .show()
                return false
            }
            TextUtils.isEmpty(maximum_pressure.text.toString()) -> {
                Snackbar.make(container, "Please enter the pressure", Snackbar.LENGTH_SHORT).show()
                return false
            }
            TextUtils.isEmpty(ci_lot.text.toString()) -> {
                Snackbar.make(container, "Enter CI Lot Number", Snackbar.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }


    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }


    override fun onTimeChanges(selectedTime: String) {
        time.text = selectedTime
    }

    override fun showErrorMessage(message: String) {
    }

    override fun addedSuccess() {
        Snackbar.make(container, "Sterilization process complete", Snackbar.LENGTH_SHORT).show()
        try {
            requireActivity().runOnUiThread {
                clearViews()
            }
        } catch (e: Exception) {

        }
    }

    private fun clearViews() {
        barcodeAdapter?.notifyDataSetChanged()
        packageItems.clear()
        operators.text = getString(R.string.select_operator)
        autoclaves.text = getString(R.string.select_autoclave)
        cycle_number.setText("")
        date.text = Utility.getCurrentDate()
        time.text = Utility.getCurrentTime()
        sterilization_duration.setText("")
        maximum_temperature.setText("")
        maximum_pressure.setText("")
        ci_lot.setText("")
    }
}