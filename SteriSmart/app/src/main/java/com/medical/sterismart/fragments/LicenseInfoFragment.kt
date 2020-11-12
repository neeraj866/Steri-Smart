package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.OfficesModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.LicenseInfoPresenter
import com.medical.sterismart.view.LicenseInfoView
import kotlinx.android.synthetic.main.dialog_iot_connect.cancel
import kotlinx.android.synthetic.main.dialog_renew_license.*
import kotlinx.android.synthetic.main.fragment_license_info.*
import kotlinx.android.synthetic.main.fragment_license_info.submit
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LicenseInfoFragment : BaseFragment(), LicenseInfoView {
    @Inject
    lateinit var licenseInfoPresenter: LicenseInfoPresenter
    var officeModel: OfficesModel? = null

    var renewCode: Int = 0
    var licenseNumber = ""
    var daysLeft = 0

    companion object {
        fun getCallingFragment(): Fragment {
            return LicenseInfoFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_license_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        licenseInfoPresenter.licenseInfoView = this@LicenseInfoFragment
        licenseInfoPresenter.initializeDb(requireContext())
        Thread {
            officeModel = licenseInfoPresenter.getOffice()
            when {
                officeModel != null -> {
                    requireActivity().runOnUiThread {
                        setData(officeModel!!)
                    }
                }
            }
        }.start()
    }

    private fun setData(officeModel: OfficesModel) {
        office_name.setText(officeModel.officeName)
        email.setText(officeModel.email)
        license_number.setText(officeModel.licenseNumber)

        val format = SimpleDateFormat("EEEE MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = format.format(Date(officeModel.endDate))
        support_expiry_date.setText(formattedDate)

        office_name.isEnabled = false
        email.isEnabled = false
        license_number.isEnabled = false
        support_expiry_date.isEnabled = false

        office_name.alpha = 0.6f
        email.alpha = 0.6f
        license_number.alpha = 0.6f
        support_expiry_date.alpha = 0.6f

        val currentDate = Date()
        val difference = Date(officeModel.endDate).time - currentDate.time
        daysLeft = (difference / (24 * 60 * 60 * 1000)).toInt()
        val finalString = "Your support license expires in $daysLeft day(s)"
        support_expiry_days.text = finalString

        when {
            daysLeft < 3 -> {
                Utility.showNotification(requireActivity(), "You have only $daysLeft for support")
            }
        }

        submit.setOnClickListener {
            when {
                daysLeft < 1 -> {
                    renewCode = 0
                    licenseNumber = ""
                    val splitString = license_number.text.trim().split("")
                    for (items in splitString) {
                        when {
                            !TextUtils.isEmpty(items) -> {
                                renewCode += items.toInt()
                                licenseNumber += items
                            }
                        }
                    }
                    renewCode += 365
                    renewCode *= 365
                    Log.e("renew_code", " $renewCode")
                    openRenewDialog()
                }
            }
        }
    }

    private fun openRenewDialog() {
        val renewDialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        renewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val lp = renewDialog.window?.attributes
        lp?.dimAmount = 0.5f

        renewDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        renewDialog.setContentView(R.layout.dialog_renew_license)

        renewDialog.cancel.setOnClickListener {
            renewDialog.dismiss()
        }

        renewDialog.submit.setOnClickListener {
            when {
                TextUtils.isEmpty(renewDialog.pin_number.text.toString()) -> {
                    Toast.makeText(requireContext(), "Please enter the PIN", Toast.LENGTH_SHORT)
                        .show()
                }
                !TextUtils.equals(renewDialog.pin_number.text.toString(), officeModel!!.pin) -> {
                    Toast.makeText(requireContext(), "Wrong PIN Number", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(renewDialog.renew_code.text.toString()) -> {
                    Toast.makeText(requireContext(),
                        "Please enter the Renew Code",
                        Toast.LENGTH_SHORT).show()
                }
                !TextUtils.equals(renewDialog.renew_code.text.toString(), renewCode.toString()) -> {
                    Toast.makeText(requireContext(), "Wrong renew code", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    renewSupport()
                    renewDialog.dismiss()
                }
            }
        }

        renewDialog.cancel!!.setOnClickListener {
            renewDialog.dismiss()
        }
        renewDialog.show()

    }

    private fun renewSupport() {
        officeModel!!.startDate = Date().toString()
        var dt = Date()
        val c = Calendar.getInstance()
        c.time = dt
        c.add(Calendar.DATE, 365)
        dt = c.time
        officeModel!!.endDate = dt.toString()

        var newLicenseNumber = (licenseNumber.toLong() + renewCode).toString()
        officeModel!!.licenseNumber = newLicenseNumber
        when {
            newLicenseNumber.length < 10 -> {
                newLicenseNumber += "9"
            }
            newLicenseNumber.length > 10 -> {
                newLicenseNumber = newLicenseNumber.substring(0, 10)
            }
        }

        license_number.setText(newLicenseNumber)

        val currentDate = Date()
        val difference = Date(officeModel!!.endDate).time - currentDate.time
        daysLeft = (difference / (24 * 60 * 60 * 1000)).toInt()
        val finalString = "Your support license expires in $daysLeft day(s)"
        support_expiry_days.text = finalString

        Thread {
            licenseInfoPresenter.updateOffice(officeModel!!)
        }.start()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun updateSuccess() {
        Snackbar.make(container, "License renewed successfully", Snackbar.LENGTH_SHORT).show()
    }
}