package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.AutoclaveModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.AutoclaveAdapter
import com.medical.sterismart.adapter.ManufacturerAdapter
import com.medical.sterismart.adapter.ModelAdapter
import com.medical.sterismart.custom.DialogUtils
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.AutoclaveSetupPresenter
import com.medical.sterismart.view.AutoclaveSetupView
import kotlinx.android.synthetic.main.dialog_manufacturer.*
import kotlinx.android.synthetic.main.dialog_model.*
import kotlinx.android.synthetic.main.fragment_autoclave_setup.*
import javax.inject.Inject

class AutoclaveSetupFragment : BaseFragment(), AutoclaveSetupView,
    AutoclaveAdapter.OnItemSelectListener, ManufacturerAdapter.OnManufacturerSelected,
    ModelAdapter.OnModelSelected, DialogUtils.OnItemSelected {

    @Inject
    lateinit var autoclaveSetupPresenter: AutoclaveSetupPresenter
    private var autoclaveManufacturer = ""
    private var autoclaveSelectedModel = ""
    private var autoclaveModel: AutoclaveModel? = null
    private var temperatureUnit = "C"
    private var pressureUnit = "PSI"
    private var modelList: MutableList<String> = ArrayList()
    private var imagesList: MutableList<Int> = ArrayList()
    private var manufacturerDialog: Dialog? = null
    private var modelDialog: Dialog? = null
    private var onAutoclaveSetupComplete: OnAutoclaveSetupComplete? = null
    private var isLoaner = 0
    private var autoclaveImage: Int = 0

    fun initializeListener(onAutoclaveSetupComplete: OnAutoclaveSetupComplete) {
        this.onAutoclaveSetupComplete = onAutoclaveSetupComplete
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_autoclave_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        autoclaveSetupPresenter.autoclaveSetupView = this@AutoclaveSetupFragment
        autoclaveSetupPresenter.initializeDb(requireContext())

        add_autoclave.setOnClickListener {
            Utility.hideKeyboard(add_autoclave)
            when {
                goFurther() -> {
                    addAutoclave()
                }
            }
        }

        update_autoclave.setOnClickListener {
            Utility.hideKeyboard(add_autoclave)
            when {
                goFurther() && autoclaveModel != null -> {
                    updateAutoclave(autoclaveModel!!)
                }
            }
        }

        container.setOnClickListener { Utility.hideKeyboard(container) }

        celcius.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    temperatureUnit = "C"
                }
            }
        }
        farenheit.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    temperatureUnit = "F"
                }
            }
        }
        psi.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    pressureUnit = "PSI"
                }
            }
        }
        kpa.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    pressureUnit = "KPA"
                }
            }
        }

        manufacture.setOnClickListener {
            showManufacturerDialog()
        }
        autoclave_model_name.setOnClickListener {
            when {
                modelList.size > 0 -> {
                    showModelDialog()
                }
            }
        }
        val bundle = arguments
        if (bundle != null) {
            when {
                bundle.containsKey("autoclave_id") && bundle.getInt("autoclave_id") != 0 -> {
                    setData(bundle.getInt("autoclave_id"))
                    update_autoclave.visibility = View.VISIBLE
                    add_autoclave.visibility = View.GONE
                }
                else -> {
                    setupModelList("Midmark")
                    update_autoclave.visibility = View.GONE
                    add_autoclave.visibility = View.VISIBLE
                }
            }
        }
        loaner_demo.setOnCheckedChangeListener { _, isChecked ->
            isLoaner = when {
                isChecked -> {
                    0
                }
                else -> {
                    1
                }
            }
        }
        autoclave_type.setOnClickListener {
            DialogUtils.showListDialog(
                requireContext(),
                resources.getString(R.string.type),
                resources.getStringArray(R.array.autoclave_type).toList(),
                this
            )
        }
        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setData(id: Int) {
        Thread {
            autoclaveModel = autoclaveSetupPresenter.getAutoclave(id)
            requireActivity().runOnUiThread {
                when {
                    autoclaveModel != null -> {
                        Utility.hideKeyboard(container)
                        manufacture.text = autoclaveModel?.manufacturer
                        autoclave_model_name.text = autoclaveModel?.model
                        name.setText(autoclaveModel?.name)
                        maximum_temperature.setText(autoclaveModel?.maximum_temperature)
                        maximum_pressure.setText(autoclaveModel?.maximum_pressure)
                        sterilization_duration.setText(autoclaveModel?.duration)
                        cycle_number.setText(autoclaveModel?.cycleNumber)
                        autoclave_type.text = autoclaveModel?.autoclaveType
                        when (autoclaveModel?.isLoaner) {
                            0 -> {
                                loaner_demo.isChecked = true
                            }
                            else -> {
                                loaner_demo.isChecked = false
                            }
                        }
                        setupModelList(autoclaveModel?.manufacturer!!)
                        when (autoclaveModel?.temperature_unit) {
                            "C" -> {
                                celcius.isChecked = true
                            }
                            "F" -> {
                                farenheit.isChecked = true
                            }
                        }
                        when (autoclaveModel?.pressure_unit) {
                            "PSI" -> {
                                psi.isChecked = true
                            }
                            "KPA" -> {
                                kpa.isChecked = true
                            }
                        }
                    }
                }
            }
        }.start()
    }

    private fun showModelDialog() {
        modelDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        modelDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = modelDialog?.window?.attributes
        lp?.dimAmount = 0.5f

        modelDialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        modelDialog?.setContentView(R.layout.dialog_model)

        val modelAdapter = ModelAdapter(requireActivity())
        modelDialog?.model_list!!.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        modelAdapter.models = modelList
        modelAdapter.images = imagesList
        modelDialog?.model_list!!.adapter = modelAdapter
        modelAdapter.onModelSelected = this

        modelDialog?.show()

        modelDialog?.cancel_model_dialog!!.setOnClickListener {
            modelDialog?.dismiss()
        }

        modelDialog?.add_model!!.setOnClickListener {
            when {
                TextUtils.isEmpty(modelDialog?.other_model_name!!.text.toString()) -> {
                    Toast.makeText(requireContext(), "Please enter the model", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    autoclaveSelectedModel = modelDialog?.other_model_name!!.text.toString()
                    autoclave_model_name.text = modelDialog?.other_model_name!!.text.toString()
                    modelDialog?.dismiss()
                }
            }
        }
    }


    private fun showManufacturerDialog() {
        manufacturerDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        manufacturerDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = manufacturerDialog?.window?.attributes
        lp?.dimAmount = 0.5f

        manufacturerDialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        manufacturerDialog?.setContentView(R.layout.dialog_manufacturer)

        val manufacturerAdapter = ManufacturerAdapter(requireActivity())
        manufacturerDialog?.manufacturer_list!!.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        manufacturerAdapter.manufacturers =
            resources.getStringArray(R.array.autoclave_manufacturer).toList()
        manufacturerDialog?.manufacturer_list!!.adapter = manufacturerAdapter
        manufacturerAdapter.onManufacturerSelected = this

        manufacturerDialog?.show()

        manufacturerDialog?.cancel_dialog!!.setOnClickListener {
            manufacturerDialog?.dismiss()
        }

        manufacturerDialog?.add!!.setOnClickListener {
            when {
                TextUtils.isEmpty(manufacturerDialog?.autoclave_model!!.text.toString()) -> {
                    Toast.makeText(requireContext(), "Please enter the model", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {

                    autoclave_model_name.text =
                        manufacturerDialog?.autoclave_model!!.text.toString()
                    manufacturerDialog?.dismiss()
                }
            }
        }
    }


    private fun clearViews() {
        requireActivity().runOnUiThread {
            name.setText("")
            maximum_temperature.setText("")
            maximum_pressure.setText("")
            sterilization_duration.setText("")
            celcius.isChecked = true
            psi.isChecked = true
            manufacture.text = ""
            autoclave_type.text.toString()
            autoclave_model_name.text = ""
        }
    }


    private fun addAutoclave() {
        var id: Int
        Thread {
            id = autoclaveSetupPresenter.getAutoclavesNextId()!!
            val autoclave = AutoclaveModel(
                id,
                0,
                autoclave_type.text.toString(),
                autoclaveManufacturer,
                autoclaveSelectedModel,
                name.text.toString(), autoclaveImage,
                maximum_temperature.text.toString(),
                temperatureUnit,
                maximum_pressure.text.toString(),
                pressureUnit,
                sterilization_duration.text.toString(),
                cycle_number.text.toString(),
                isLoaner
            )
            autoclaveSetupPresenter.addAutoclave(autoclave)
        }.start()
    }

    private fun updateAutoclave(autoclaveModel: AutoclaveModel) {
        Thread {
            val autoclave = AutoclaveModel(
                autoclaveModel.id,
                0,
                autoclave_type.text.toString(),
                autoclaveManufacturer,
                autoclaveSelectedModel,
                name.text.toString(), autoclaveImage,
                maximum_temperature.text.toString(),
                temperatureUnit,
                maximum_pressure.text.toString(),
                pressureUnit,
                sterilization_duration.text.toString(),
                cycle_number.text.toString(),
                isLoaner
            )
            autoclaveSetupPresenter.updateAutoclave(autoclave)
        }.start()
    }


    private fun goFurther(): Boolean {
        when {
            TextUtils.isEmpty(name.text.toString()) -> {
                Toast.makeText(
                    requireContext(),
                    "Please enter the name of autoclave",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            TextUtils.isEmpty(manufacture.text.toString()) -> {
                Toast.makeText(
                    requireContext(),
                    "Please select the manufacturer",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(autoclave_model_name.text.toString()) -> {
                Toast.makeText(requireContext(), "Please select the model", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return true
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {
    }

    override fun addedSuccess() {
        Snackbar.make(container, "Autoclave added successfully", Snackbar.LENGTH_SHORT).show()
        clearViews()
        onAutoclaveSetupComplete?.autoclaveComplete()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun updateSuccess() {
        Snackbar.make(container, "Autoclave updated successfully", Snackbar.LENGTH_SHORT).show()
        clearViews()
        onAutoclaveSetupComplete?.autoclaveComplete()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun deletedSuccess() {
        Snackbar.make(container, "Autoclave deleted successfully", Snackbar.LENGTH_SHORT).show()
        clearViews()
    }

    override fun onItemSelected(autoclaveModel: AutoclaveModel, isDeleted: Boolean) {

    }


    override fun selectedManufacturer(manufacturerText: String) {
        setupModelList(manufacturerText)
    }

    private fun setupModelList(manufacturerText: String) {
        var isDialogDismiss = false
        manufacture.text = manufacturerText
        autoclaveManufacturer = manufacturerText
        modelList.clear()
        imagesList.clear()
        when (manufacturerText) {
            "Midmark" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.midmark_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.midmark_models)[0]
                modelList.addAll(resources.getStringArray(R.array.midmark_models).toList())
                autoclaveImage = R.drawable.midmark_11
                imagesList.add(R.drawable.midmark_11)
                imagesList.add(R.drawable.midmark)
                imagesList.add(R.drawable.midmark)
                imagesList.add(R.drawable.midmark_11)
                imagesList.add(R.drawable.midmark)
                imagesList.add(R.drawable.midmark)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Scican" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.scican_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.scican_models)[0]
                modelList.addAll(resources.getStringArray(R.array.scican_models).toList())
                autoclaveImage = R.drawable.bravo
                imagesList.add(R.drawable.bravo)
                imagesList.add(R.drawable.statclave_g4)
                imagesList.add(R.drawable.statim_2000)
                imagesList.add(R.drawable.statim_5000)
                imagesList.add(R.drawable.statim_7000)
                imagesList.add(R.drawable.statim_g4_2000)
                imagesList.add(R.drawable.statim_g4_5000)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Masteri" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.masteri_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.masteri_models)[0]
                modelList.addAll(resources.getStringArray(R.array.masteri_models).toList())
                autoclaveImage = R.drawable.masteri
                imagesList.add(R.drawable.masteri)
                imagesList.add(R.drawable.masteri)
                imagesList.add(R.drawable.masteri)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Flight Dental" -> {
                isDialogDismiss = true
                autoclave_model_name.text =
                    resources.getStringArray(R.array.flight_dental_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.flight_dental_models)[0]
                modelList.addAll(resources.getStringArray(R.array.flight_dental_models).toList())
                autoclaveImage = R.drawable.flight_16
                imagesList.add(R.drawable.flight_16)
                imagesList.add(R.drawable.flight_23)
                imagesList.add(R.drawable.flight_dental)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Ritter" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.ritter_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.ritter_models)[0]
                modelList.addAll(resources.getStringArray(R.array.ritter_models).toList())
                autoclaveImage = R.drawable.ritter
                imagesList.add(R.drawable.ritter)
                imagesList.add(R.drawable.midmark_11)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Tuttnauer" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.tuttnauer_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.tuttnauer_models)[0]
                modelList.addAll(resources.getStringArray(R.array.tuttnauer_models).toList())
                autoclaveImage = R.drawable.tuttnauer
                imagesList.add(R.drawable.tuttnauer)
                imagesList.add(R.drawable.tuttnauer)
                imagesList.add(R.drawable.tuttnauer_2340_m)
                imagesList.add(R.drawable.tuttnauer_2340_m)
                imagesList.add(R.drawable.tuttnauer_2540_m)
                imagesList.add(R.drawable.tuttnauer_2540_m)
                imagesList.add(R.drawable.tuttnauer_2540_m)
                imagesList.add(R.drawable.tuttnauer_2540_mk)
                imagesList.add(R.drawable.tuttnauer_3870m)
                imagesList.add(R.drawable.tuttnauer_3870m)
                imagesList.add(R.drawable.tuttnauer_3870m)
                imagesList.add(R.drawable.elara_11)
                imagesList.add(R.drawable.ez_9)
                imagesList.add(R.drawable.ez_9_plus)
                imagesList.add(R.drawable.ez10)
                imagesList.add(R.drawable.ez10k)
                imagesList.add(R.drawable.ez_11_plus)
                imagesList.add(R.drawable.tvet_10m)
                imagesList.add(R.drawable.tvet_10e)
                imagesList.add(R.drawable.tvet_10m)
                imagesList.add(R.drawable.tvet_11e)
                imagesList.add(R.drawable.valueklave_1730)
                imagesList.add(R.drawable.other_autoclave)
            }

            "W&H Lisa" -> {
                isDialogDismiss = true
                autoclave_model_name.text = resources.getStringArray(R.array.w_and_h_lisa_models)[0]
                autoclaveSelectedModel = resources.getStringArray(R.array.w_and_h_lisa_models)[0]
                modelList.addAll(resources.getStringArray(R.array.w_and_h_lisa_models).toList())
                autoclaveImage = R.drawable.w_h_lina
                imagesList.add(R.drawable.w_h_lina)
                imagesList.add(R.drawable.w_h_lisa)
                imagesList.add(R.drawable.other_autoclave)
            }

            "Other" -> {
                manufacturerDialog?.manufacturer_list!!.scrollToPosition(8)
                manufacturerDialog?.other_model!!.visibility = View.VISIBLE
                manufacturerDialog?.autoclave_model!!.visibility = View.VISIBLE
                autoclaveImage = R.drawable.other_autoclave
            }
        }
        when {
            isDialogDismiss -> {
                manufacturerDialog?.dismiss()
            }
        }
    }

    override fun selectedModel(modelText: String, image: Int) {
        when {
            modelText != "Other" -> {
                modelDialog?.dismiss()
                autoclave_model_name!!.text = modelText
                autoclaveSelectedModel = modelText
                autoclaveImage = image
            }
            else -> {
                modelDialog?.model_list!!.scrollToPosition(modelList.size - 1)
                modelDialog?.other_model_text!!.visibility = View.VISIBLE
                modelDialog?.other_model_name!!.visibility = View.VISIBLE
                autoclaveImage = R.drawable.other_autoclave
            }
        }
    }

    interface OnAutoclaveSetupComplete {
        fun autoclaveComplete()
    }

    override fun selectedItem(item: String) {
        autoclave_type.text = item
    }
}