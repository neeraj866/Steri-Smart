package com.medical.sterismart.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.DataModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.ResetDataPresenter
import com.medical.sterismart.view.ResetDataView
import kotlinx.android.synthetic.main.dialog_delete.*
import kotlinx.android.synthetic.main.fragment_reset_data.*
import javax.inject.Inject

class ResetDataFragment : BaseFragment(), ResetDataView {

    @Inject
    lateinit var resetDataPresenter: ResetDataPresenter

    var officePin = ""

    companion object {
        fun getCallingFragment(): Fragment {
            return ResetDataFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reset_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        resetDataPresenter.initializeDb(requireContext())
        resetDataPresenter.resetDataView = this

        Thread {
            officePin = resetDataPresenter.getOfficePin()
        }.start()

        start_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    start_date.text = dateString
                }
            })
        }

        end_date.setOnClickListener {
            Utility.chooseDate(requireContext(), object : Utility.OnDateSelectListener {
                override fun selectedDate(dateString: String) {
                    end_date.text = dateString
                }
            })
        }

        restore_data.setOnClickListener {
            when {
                TextUtils.isEmpty(officePin) -> {
                    Snackbar.make(container, "Please setup the office first", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    showDialog(true)
                }
            }
        }

        date_data_reset.setOnClickListener {
            when {
                TextUtils.isEmpty(start_date.text.toString()) -> {
                    Snackbar.make(container, "Please select the start date", Snackbar.LENGTH_SHORT)
                        .show()
                }
                TextUtils.isEmpty(end_date.text.toString()) -> {
                    Snackbar.make(container, "Please select the end date", Snackbar.LENGTH_SHORT)
                        .show()
                }
                TextUtils.isEmpty(officePin) -> {
                    Snackbar.make(container, "Please setup the office first", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    showDialog(false)
                }
            }
        }
    }

    private fun askAdminPasswordDialog(allDataWipe: Boolean) {
        val dialog = Utility.initializeDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_password)
        val submitBtn = dialog.findViewById(R.id.submit) as Button
        val cancelBtn = dialog.findViewById(R.id.cancel) as ImageView
        val pinNumber = dialog.findViewById(R.id.pin_number) as EditText

        submitBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(pinNumber.text.toString()) -> {
                    Snackbar.make(container, "Please enter the pin", Snackbar.LENGTH_SHORT).show()
                }
                !TextUtils.equals(pinNumber.text.toString(), officePin) -> {
                    Snackbar.make(container, "Wrong pin", Snackbar.LENGTH_SHORT).show()
                }
                allDataWipe -> {
                    dialog.dismiss()
                    Thread {
                        resetDataPresenter.getAllData()
                    }.start()
                }
                else -> {
                    dialog.dismiss()
                    Thread {
                        resetDataPresenter.getDataExceptDates(
                            Utility.convertDate(start_date.text.toString()),
                            Utility.convertDate(end_date.text.toString())
                        )
                    }.start()
                }
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showDialog(allDataWipe: Boolean) {
        val dialog = Utility.initializeDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_delete)
        dialog.delete_title.text = "Do you really want to reset the data?"
        val yesBtn = dialog.findViewById(R.id.yes) as Button
        val noBtn = dialog.findViewById(R.id.no) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            when {
                allDataWipe -> {
                    askAdminPasswordDialog(allDataWipe)
                }
                else -> {
                    askAdminPasswordDialog(false)

                }
            }
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {
    }

    override fun deletedSuccess() {
        Snackbar.make(container, "Data Reset Successfully", Snackbar.LENGTH_SHORT).show()
        requireActivity().runOnUiThread {
            start_date.text = ""
            end_date.text = ""
        }
    }

    override fun getDataForDelete(dataModel: List<DataModel>) {
        resetDataPresenter.deleteAllDate(dataModel)
    }
}