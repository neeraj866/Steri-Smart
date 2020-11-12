package com.medical.sterismart.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.DataModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_b_i_test_data.view.*

class BITestAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<BITestAdapter.DataAdapterViewHolder>() {

    var dataModels: MutableList<DataModel> = ArrayList()

    var onTestDataSelectListener: OnTestDataSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_b_i_test_data, parent, false)
        return DataAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataModels.count()
    }

    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        val dataModel = dataModels[position]

        holder.itemView.cycle_operator.text = dataModel.cycleUsername
        holder.itemView.cycle_number.text = dataModel.cycleNumber
        holder.itemView.autoclave.text = dataModel.autoclave
        holder.itemView.manufacture.text = dataModel.cycleAutoclaveModel
        holder.itemView.cycle_date.text = dataModel.cycleDate
        when (dataModel.biResult) {
            "Pending" -> {
                holder.itemView.b_i_result.text = dataModel.biResult
                holder.itemView.b_i_result.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.red
                    )
                )
            }
            else -> {
                holder.itemView.b_i_result.text = "Completed"
                holder.itemView.b_i_result.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.dark_green
                    )
                )
            }
        }

        holder.itemView.data_container.setOnClickListener {
            when {
                TextUtils.equals(dataModels[position].biResult, "Pending") -> {
                    onTestDataSelectListener?.selectedData(dataModels[position].id)
                }
            }
        }
    }

    class DataAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnTestDataSelectListener {
        fun selectedData(id: Int)
    }
}