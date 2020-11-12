package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.medical.data.tables.DataModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.BITestAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.BITestPresenter
import com.medical.sterismart.view.BITestView
import kotlinx.android.synthetic.main.fragment_b_i_list.*
import javax.inject.Inject

class BIListFragment : BaseFragment(), BITestView, BITestAdapter.OnTestDataSelectListener,
    BITestFragment.TestUpdateListener {

    @Inject
    lateinit var biTestPresenter: BITestPresenter

    private var biTestAdapter: BITestAdapter? = null
    private var dataList: MutableList<DataModel> = ArrayList()


    companion object {
        fun getCallingFragment(): Fragment {
            return BIListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_b_i_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseDependency()
        biTestPresenter.initializeDb(requireContext())

        biTestPresenter.biTestView = this

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setBITestAdapter()
        setBITestData()
    }

    private fun setBITestAdapter() {
        biTestAdapter = BITestAdapter(activity)
        b_i_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        b_i_list.isNestedScrollingEnabled = false
        biTestAdapter?.dataModels = dataList
        biTestAdapter?.onTestDataSelectListener = this
        b_i_list.adapter = biTestAdapter
    }

    private fun setBITestData() {
        dataList.clear()
        Thread {
            when {
                biTestPresenter.getAllDataModels(Utility.getCurrentDate()) != null -> {
                    dataList.addAll(biTestPresenter.getAllDataModels(Utility.getCurrentDate())!!)
                    dataList.sortBy { it.biResult }
                    dataList.reverse()
                    requireActivity().runOnUiThread {
                        biTestAdapter?.notifyDataSetChanged()
                        b_i_list.visibility = View.VISIBLE
                        no_data.visibility = View.GONE
                    }
                }
                else -> {
                    requireActivity().runOnUiThread {
                        b_i_list.visibility = View.GONE
                        no_data.visibility = View.VISIBLE
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

    override fun showErrorMessage(message: String) {

    }

    override fun updateSuccess() {

    }

    override fun selectedData(id: Int) {
        navigator.loadBITestFragment(requireActivity(), id, this)
    }

    override fun onTestUpdate() {
        setBITestData()
    }
}