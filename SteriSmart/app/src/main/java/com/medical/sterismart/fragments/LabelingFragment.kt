package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.SterilizationLabelAdapter
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.SterilizationPackagePresenter
import com.medical.sterismart.view.SterilizationPackageView
import kotlinx.android.synthetic.main.fragment_labeling.*
import javax.inject.Inject

class LabelingFragment : BaseFragment(), SterilizationPackageView {

    private var packageList: MutableList<SterilizationPackagesModel> = ArrayList()

    @Inject
    lateinit var sterilizationPackagePresenter: SterilizationPackagePresenter
    private var sterilizationLabelAdapter: SterilizationLabelAdapter? = null

    companion object {
        fun getCallingFragment(): Fragment {
            return LabelingFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_labeling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        sterilizationPackagePresenter.initializeDb(requireContext())
        sterilizationPackagePresenter.sterilizationPackageView = this
        setAdapter()
        getPackages()
    }

    private fun setAdapter() {
        sterilizationLabelAdapter = SterilizationLabelAdapter(requireActivity())
        label_list.layoutManager = GridLayoutManager(requireContext(), 2)
        sterilizationLabelAdapter?.packages = packageList
        label_list.adapter = sterilizationLabelAdapter
    }

    private fun getPackages() {
        Thread {
            when {
                sterilizationPackagePresenter.getSterilizationPackages() != null -> {
                    packageList.clear()
                    packageList.addAll(sterilizationPackagePresenter.getSterilizationPackages()!!)
                    requireActivity().runOnUiThread {
                        sterilizationLabelAdapter?.notifyDataSetChanged()
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


    override fun addedSuccess() {

    }

    override fun deletedSuccess() {

    }
}