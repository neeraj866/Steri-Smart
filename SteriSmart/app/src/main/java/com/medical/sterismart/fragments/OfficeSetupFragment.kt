package com.medical.sterismart.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.OfficesModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.OfficePresenter
import com.medical.sterismart.view.OfficeView
import kotlinx.android.synthetic.main.fragment_office_setup.*
import java.util.*
import javax.inject.Inject

class OfficeSetupFragment : BaseFragment(), OfficeView {

    @Inject
    lateinit var officePresenter: OfficePresenter

    private var officeModelMain: OfficesModel? = null
    private var firstTimeSetup: Boolean = false

    companion object {
        fun getCallingFragment(): Fragment {
            return OfficeSetupFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_office_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }
        initialiseDependency()
        officePresenter.officeVIew = this@OfficeSetupFragment
        officePresenter.initializeDb(requireContext())
        Thread {
            officeModelMain = officePresenter.getOffice()
            when {
                officeModelMain != null -> {
                    requireActivity().runOnUiThread {
                        setData(officeModelMain!!)
                    }
                }
                else -> {
                    firstTimeSetup = true
                }
            }
        }.start()

        update_button.setOnClickListener {
            checkAllDataAndSubmit()
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

    private fun checkAllDataAndSubmit() {
        Utility.hideKeyboard(update_button)
        when {
            TextUtils.isEmpty(office_name.text) -> {
                office_name.error = "Enter Office name"
                Snackbar.make(container, "Enter Office name", Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                makeOfficeModel()
            }
        }
    }

    private fun makeOfficeModel() {
        var startDate = ""
        var endDate = ""
        when {
            officeModelMain != null -> {
                startDate = officeModelMain!!.startDate
                endDate = officeModelMain!!.endDate
            }
        }
        officeModelMain = OfficesModel(
            1,
            "",
            city.text.toString(),
            "",
            "",
            endDate,
            0,
            "",
            "",
            office_name.text.toString(),
            "",
            postal_code.text.toString(),
            "",
            "",
            startDate,
            street_address.text.toString(),
            "",
            0
        )
        when {
            firstTimeSetup -> {
                officeModelMain!!.startDate = Date().toString()
                var dt = Date()
                val c = Calendar.getInstance()
                c.time = dt
                c.add(Calendar.DATE, 365)
                dt = c.time
                officeModelMain!!.endDate = dt.toString()
                officePresenter.addOffice(officeModelMain!!)
            }
            else -> {
                officePresenter.updateOffice(officeModelMain!!)
            }
        }
    }

    private fun setData(officeModel: OfficesModel) {
        office_name.setText(officeModel.officeName)
        street_address.setText(officeModel.streetAddress)
        city.setText(officeModel.city)
        postal_code.setText(officeModel.postalCode)
    }

    override fun showErrorMessage(message: String) {

    }

    override fun updateSuccess() {
        Snackbar.make(container, "Office setup is complete", Snackbar.LENGTH_SHORT).show()
    }
}