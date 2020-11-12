package com.medical.sterismart.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.DataModel
import com.medical.data.tables.UsersModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.DialogUtils
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.BITestPresenter
import com.medical.sterismart.view.BITestView
import kotlinx.android.synthetic.main.fragment_bi_test.*
import javax.inject.Inject

class BITestFragment : BaseFragment(), BITestView {

    @Inject
    lateinit var biTestPresenter: BITestPresenter

    private var tempDataModel: DataModel? = null
    private var operatorsList: MutableList<UsersModel>? = null

    private var userId = ""
    private var userName = ""

    private var testUpdateListener: TestUpdateListener? = null

    private var biTestResult = "Pass"
    private var typeOneStatus = "Pass"
    private var typeFourStatus = "Pass"
    private var typeFiveStatus = "Pass"

    companion object {
        fun getCallingFragment(): Fragment {
            return BITestFragment()
        }
    }

    fun initializeTestListener(testUpdateListener: TestUpdateListener) {
        this.testUpdateListener = testUpdateListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bi_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseDependency()
        biTestPresenter.initializeDb(requireContext())
        biTestPresenter.biTestView = this
        getOperators()

        when {
            arguments != null && requireArguments().containsKey("process_id") -> {
                setData(requireArguments().getInt("process_id"))
            }
        }



        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }

        submit.setOnClickListener {
            when {
                goFurther() -> {
                    updateTestDetails()
                }
            }
        }

        pass.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    biTestResult = "Pass"
                }
            }
        }

        fail.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    biTestResult = "Fail"
                }
            }
        }

        operators.setOnClickListener {

        }

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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

        type_one_pass.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeOneStatus = "PASS"
                }
            }
        }
        type_one_fail.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeOneStatus = "FAIL"
                }
            }
        }
        type_four_pass.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeFourStatus = "PASS"
                }
            }
        }
        type_four_fail.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeFourStatus = "FAIL"
                }
            }
        }
        type_five_pass.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeFiveStatus = "PASS"
                }
            }
        }
        type_five_fail.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    typeFiveStatus = "FAIL"
                }
            }
        }
    }

    private fun getOperators() {
        Thread {
            when {
                biTestPresenter.getUsers() != null -> {
                    operatorsList = biTestPresenter.getUsers()
                }
            }
        }.start()
    }

    private fun setData(id: Int) {
        var dataModel: DataModel?
        Thread {
            when {
                biTestPresenter.getDataModel(id) != null -> {
                    dataModel = biTestPresenter.getDataModel(id)
                    when {
                        dataModel != null -> {
                            requireActivity().runOnUiThread {
                                tempDataModel = dataModel
                                tempDataModel?.biTime = dataModel?.cycleDate!!

                            }
                        }
                    }
                }
            }
        }.start()
    }

    private fun updateTestDetails() {
        tempDataModel?.biUsername = userName
        tempDataModel?.biResult = biTestResult
        tempDataModel?.biLotNumber = lot_number.text.toString()
        tempDataModel?.biUserId = userId
        tempDataModel?.typeOneStatus = typeOneStatus
        tempDataModel?.typeFourStatus = typeFourStatus
        tempDataModel?.typeFiveStatus = typeFiveStatus
        biTestPresenter.updateData(tempDataModel!!)
    }

    private fun goFurther(): Boolean {
        when {
            operators.text.toString() == "Select Operator" -> {
                Snackbar.make(container, "Please select the operator", Snackbar.LENGTH_SHORT).show()
                return false
            }
            tempDataModel == null -> {
                Snackbar.make(container, "No test is selected", Snackbar.LENGTH_SHORT).show()
                return false
            }
            TextUtils.isEmpty(lot_number.text.toString()) -> {
                Snackbar.make(container, "Please enter B. I. Lot Number", Snackbar.LENGTH_SHORT)
                    .show()
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
                                userId = items.id.toString()
                                break@loop
                            }
                        }
                    }
                    operators.text = item
                    userName = item
                }

            })
    }

    override fun showErrorMessage(message: String) {

    }

    override fun updateSuccess() {
        Snackbar.make(container, "Test Update successfully", Snackbar.LENGTH_SHORT).show()
        requireActivity().runOnUiThread {
            testUpdateListener?.onTestUpdate()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    interface TestUpdateListener {
        fun onTestUpdate()
    }
}