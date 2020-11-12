package com.medical.sterismart.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.UsersModel
import com.medical.sterismart.R
import com.medical.sterismart.adapter.OperatorAdapter
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.OperatorSetupPresenter
import com.medical.sterismart.view.OperatorSetupView
import kotlinx.android.synthetic.main.dialog_add_operator.*
import kotlinx.android.synthetic.main.fragment_operator_setup.*
import javax.inject.Inject

class OperatorSetupFragment : BaseFragment(), OperatorSetupView,
    OperatorAdapter.OnOperatorItemSelectListener {

    @Inject
    lateinit var operatorSetupPresenter: OperatorSetupPresenter
    private val userModels: MutableList<UsersModel> = ArrayList()
    private var operatorAdapter: OperatorAdapter? = null
    private var usersModel: UsersModel? = null
    private var isUpdating = false
    private var signature = ""

    companion object {
        fun getCallingFragment(): Fragment {
            return OperatorSetupFragment()
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
        operatorSetupPresenter.operatorSetupView = this@OperatorSetupFragment
        operatorSetupPresenter.initializeDb(requireContext())


        add.setOnClickListener {
            Utility.hideKeyboard(add)
            isUpdating = false
            showAddDialog()
        }

        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setOperatorAdapter()
        setData()
    }

    private var addDialog: Dialog? = null
    private fun showAddDialog() {
        addDialog = Utility.initializeDialog(requireActivity())
        addDialog?.setContentView(R.layout.dialog_add_operator)

        when {
            isUpdating -> {
                addDialog?.first_name!!.setText(usersModel?.firstName)
                addDialog?.last_name!!.setText(usersModel?.lastName)
                addDialog?.add_operator!!.text = getString(R.string.update)
                when {
                    !TextUtils.isEmpty(usersModel?.signature) -> {
                        signature = usersModel?.signature!!
                        addDialog?.signature!!.mBitmap =
                            Utility.stringToBitmap(usersModel?.signature!!)
                    }
                }
            }
        }

        addDialog?.clear_view!!.setOnClickListener {
            addDialog?.signature!!.clear()
        }

        addDialog?.add_operator!!.setOnClickListener {
            when {
                TextUtils.isEmpty(addDialog?.first_name!!.text.toString()) -> {
                    Toast.makeText(requireContext(), "Please enter First Name", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    signature = Utility.bitmapToString(addDialog?.signature!!.mBitmap!!)!!
                    when {
                        isUpdating -> {
                            updateUser(
                                addDialog?.first_name!!.text.toString(),
                                addDialog?.last_name!!.text.toString()
                            )
                        }
                        else -> {
                            addUser(
                                addDialog?.first_name!!.text.toString(),
                                addDialog?.last_name!!.text.toString()
                            )
                        }
                    }
                }
            }
        }
        addDialog?.cancel!!.setOnClickListener {
            addDialog?.dismiss()
        }
        addDialog?.show()
    }

    private fun updateUser(firstName: String, lastName: String) {
        usersModel?.firstName = firstName
        usersModel?.lastName = lastName
        usersModel?.signature = signature
        operatorSetupPresenter.updateOperator(usersModel!!)
    }


    private fun setOperatorAdapter() {
        operatorAdapter = OperatorAdapter(activity)
        operator_list.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        operator_list.adapter = operatorAdapter
        operatorAdapter?.onItemSelectListener = this
    }

    private fun setData() {
        userModels.clear()
        Thread {
            when {
                operatorSetupPresenter.getActiveOperators() != null -> {
                    userModels.addAll(operatorSetupPresenter.getActiveOperators()!!)
                }
            }
            requireActivity().runOnUiThread {
                when {
                    userModels.size > 0 -> {
                        no_data.visibility = View.GONE
                        operator_list.visibility = View.VISIBLE
                    }
                    else -> {
                        no_data.visibility = View.VISIBLE
                        operator_list.visibility = View.GONE
                    }
                }
                operatorAdapter?.userItems = userModels
                operatorAdapter?.notifyDataSetChanged()
            }
        }.start()
    }

    private fun showDialog() {
        val dialog = Utility.initializeDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_delete)
        val yesBtn = dialog.findViewById(R.id.yes) as Button
        val noBtn = dialog.findViewById(R.id.no) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            usersModel?.activeStatus = 0
            operatorSetupPresenter.deleteOperator(usersModel!!)
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun addUser(firstName: String, lastName: String) {
        var id: Int
        Thread {
            id = operatorSetupPresenter.getOperatorNextId()!!
            val user = UsersModel(id, 1, firstName, lastName, signature)
            operatorSetupPresenter.addOperator(user)
        }.start()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(container, "Something went wrong", Snackbar.LENGTH_SHORT).show()
    }

    override fun addedSuccess() {
        Snackbar.make(container, "Operator added successfully", Snackbar.LENGTH_SHORT).show()
        setData()
        requireActivity().runOnUiThread {
            addDialog?.dismiss()
        }
    }

    override fun updateSuccess() {
        Snackbar.make(container, "Operator updated successfully", Snackbar.LENGTH_SHORT).show()
        setData()
        requireActivity().runOnUiThread {
            addDialog?.dismiss()
        }
    }

    override fun deletedSuccess() {
        Snackbar.make(container, "Operator deleted" + " successfully", Snackbar.LENGTH_SHORT).show()
        Thread.sleep(100)
        setData()
    }

    override fun onItemSelected(usersModel: UsersModel, isDeleted: Boolean) {
        this.usersModel = usersModel
        when {
            isDeleted -> {
                showDialog()
            }
            else -> {
                isUpdating = true
                showAddDialog()
            }
        }
    }
}