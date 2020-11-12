package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.PackageAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.PackagePresenter
import com.medical.sterismart.view.PackageView
import kotlinx.android.synthetic.main.fragment_operator_setup.*
import javax.inject.Inject

class PackageListFragment : BaseFragment(), PackageView, PackageAdapter.OnPackageSelectedListener,
    CreateNewPackageFragment.OnPackageChangeListener {

    @Inject
    lateinit var packagePresenter: PackagePresenter

    private var packageAdapter: PackageAdapter? = null
    private var packageItems: MutableList<PackagesModel> = ArrayList()
    private var selectedPackage: PackagesModel? = null

    companion object {
        fun getCallingFragment(): Fragment {
            return PackageListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_operator_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        packagePresenter.initializeDb(requireContext())
        packagePresenter.packageView = this

        title.text = getString(R.string.packages)
        add.text = getString(R.string.add_package)

        add.setOnClickListener {
            navigator.loadCreateNewPackageFragment(requireActivity(), this, 0)
        }
        setAdapter()
        setData()
        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setAdapter() {
        packageAdapter = PackageAdapter(requireActivity())
        operator_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        packageAdapter?.packageItems = packageItems
        packageAdapter?.onPackageSelectedListener = this
        operator_list.adapter = packageAdapter
    }

    private fun setData() {
        packageItems.clear()
        Thread {
            when {
                packagePresenter.getPackages() != null -> {
                    packageItems.addAll(packagePresenter.getPackages()!!)
                }
            }
            requireActivity().runOnUiThread {
                packageAdapter?.notifyDataSetChanged()
            }
        }.start()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {

    }

    override fun addedSuccess() {

    }

    override fun updateSuccess() {

    }

    override fun deletedSuccess() {
        setData()
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), "Package deleted successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun deletePackage(packagesModel: PackagesModel) {
        selectedPackage = packagesModel
        when {
            selectedPackage != null -> {
                showDeleteDialog()
            }
        }
    }

    override fun editPackage(packagesModel: PackagesModel) {
        selectedPackage = packagesModel
        navigator.loadCreateNewPackageFragment(requireActivity(), this, selectedPackage!!.id)
    }


    private fun showDeleteDialog() {
        val dialog = Utility.initializeDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_delete)
        val yesBtn = dialog.findViewById(R.id.yes) as Button
        val noBtn = dialog.findViewById(R.id.no) as Button
        val title = dialog.findViewById(R.id.delete_title) as TextView
        title.text = getString(R.string.do_you_really_want_to_delete_this_package)
        yesBtn.setOnClickListener {
            dialog.dismiss()
            packagePresenter.deletePackage(selectedPackage!!)
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onPackageChanged() {
        setData()
    }
}