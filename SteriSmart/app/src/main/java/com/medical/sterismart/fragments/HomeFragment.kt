package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.dialog_login_key.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    companion object {
        fun getCallingFragment(): Fragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        when {
            TextUtils.isEmpty(Utility.getKey(requireContext())) -> {
                showLoginKeyDialog()
            }
        }

        packaging_labeling.setOnClickListener {
            navigator.loadSterilizationPackageFragment(requireActivity())
        }
        sterilization.setOnClickListener {
            navigator.loadSterilizationFragment(requireActivity())
        }
        b_i_test.setOnClickListener {
            navigator.loadBIListFragment(requireActivity())
        }
        reporting.setOnClickListener {
            navigator.loadReportMenuFragment(requireActivity())
        }
        autoclave.setOnClickListener {
            navigator.loadAutoclaveFragment(requireActivity())
        }
        packages.setOnClickListener {
            navigator.loadPackageListFragment(requireActivity())
        }
        operators.setOnClickListener {
            navigator.loadOperatorSetupFragment(requireActivity())
        }
        settings.setOnClickListener {
            navigator.loadSettingsFragment(requireActivity())
        }
    }

    private var keyDialog: Dialog? = null
    private fun showLoginKeyDialog() {
        keyDialog = Utility.initializeDialog(requireActivity())
        keyDialog?.setContentView(R.layout.dialog_login_key)


        keyDialog?.submit!!.setOnClickListener {
            when {
                TextUtils.isEmpty(keyDialog?.login_key!!.text.toString()) -> {
                    Toast.makeText(requireContext(), "Please enter the key", Toast.LENGTH_SHORT)
                        .show()
                }
                TextUtils.equals(keyDialog?.login_key!!.text.toString(), "Harleenlove@1234") -> {
                    Utility.saveKey(requireContext(), keyDialog?.login_key!!.text.toString())
                    keyDialog?.dismiss()
                }
                else -> {
                    Toast.makeText(requireContext(), "Wrong key", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        keyDialog?.show()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }
}