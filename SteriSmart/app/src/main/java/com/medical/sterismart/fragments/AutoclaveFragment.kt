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
import com.medical.data.tables.AutoclaveModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.AutoclaveAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.AutoclaveSetupPresenter
import com.medical.sterismart.view.AutoclaveSetupView
import kotlinx.android.synthetic.main.fragment_autoclave.*
import javax.inject.Inject

class AutoclaveFragment : BaseFragment(), AutoclaveSetupView, AutoclaveAdapter.OnItemSelectListener,
    AutoclaveSetupFragment.OnAutoclaveSetupComplete {

    @Inject
    lateinit var autoclaveSetupPresenter: AutoclaveSetupPresenter
    private var autoclaveAdapter: AutoclaveAdapter? = null
    private val autoclaveModels: MutableList<AutoclaveModel> = ArrayList()
    private var autoclaveModel: AutoclaveModel? = null

    companion object {
        fun getCallingFragment(): Fragment {
            return AutoclaveFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_autoclave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        autoclaveSetupPresenter.autoclaveSetupView = this
        autoclaveSetupPresenter.initializeDb(requireContext())
        setAutoclaveAdapter()
        setData()
        add_autoclave.setOnClickListener {
            navigator.loadAutoclaveSetupFragment(requireActivity(), 0, this)
        }

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    private fun setAutoclaveAdapter() {
        autoclaveAdapter = AutoclaveAdapter(activity)
        autoclave_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        autoclave_list.adapter = autoclaveAdapter
        autoclaveAdapter?.onItemSelectListener = this
    }

    private fun setData() {
        autoclaveModels.clear()
        Thread {
            when {
                autoclaveSetupPresenter.getAutoclaves() != null -> {
                    autoclaveModels.addAll(autoclaveSetupPresenter.getAutoclaves()!!)
                }
            }
            requireActivity().runOnUiThread {
                when {
                    autoclaveModels.size > 0 -> {
                        autoclaveAdapter?.autoclaveItems = autoclaveModels
                        no_autoclave.visibility = View.GONE
                    }
                    else -> {
                        no_autoclave.visibility = View.VISIBLE
                    }
                }
                autoclaveAdapter?.notifyDataSetChanged()
            }
        }.start()
    }

    override fun showErrorMessage(message: String) {

    }

    override fun addedSuccess() {

    }

    override fun updateSuccess() {

    }

    override fun deletedSuccess() {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), "Autoclave deleted successfully", Toast.LENGTH_SHORT)
                .show()
            setData()
        }
    }

    override fun onItemSelected(autoclaveModel: AutoclaveModel, isDeleted: Boolean) {
        this.autoclaveModel = autoclaveModel
        when (isDeleted) {
            true -> {
                showDialog()
            }
            else -> {
                navigator.loadAutoclaveSetupFragment(requireActivity(), autoclaveModel.id, this)
            }
        }
    }

    private fun showDialog() {
        val dialog = Utility.initializeDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_delete)
        val yesBtn = dialog.findViewById(R.id.yes) as Button
        val noBtn = dialog.findViewById(R.id.no) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            autoclaveSetupPresenter.deleteAutoclave(autoclaveModel!!)
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun autoclaveComplete() {
        setData()
    }
}