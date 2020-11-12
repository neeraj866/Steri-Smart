package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.AutoclaveModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_autoclave.view.*

class AutoclaveAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<AutoclaveAdapter.AutoclaveAdapterViewHolder>() {

    var autoclaveItems: MutableList<AutoclaveModel> = ArrayList()

    var onItemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoclaveAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_autoclave, parent, false)
        return AutoclaveAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return autoclaveItems.size
    }

    override fun onBindViewHolder(holder: AutoclaveAdapterViewHolder, position: Int) {
        val autoclaveItem = autoclaveItems[position]
        holder.itemView.name.text = autoclaveItem.name
        holder.itemView.title.text = autoclaveItem.manufacturer
        holder.itemView.model.text = autoclaveItem.model
        holder.itemView.type.text = autoclaveItem.autoclaveType


        holder.itemView.edit.setOnClickListener {
            onItemSelectListener?.onItemSelected(autoclaveItems[position], false)
        }
        holder.itemView.delete.setOnClickListener {
            onItemSelectListener?.onItemSelected(autoclaveItems[position], true)
        }
    }

    class AutoclaveAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemSelectListener {
        fun onItemSelected(autoclaveModel: AutoclaveModel, isDeleted: Boolean)
    }
}