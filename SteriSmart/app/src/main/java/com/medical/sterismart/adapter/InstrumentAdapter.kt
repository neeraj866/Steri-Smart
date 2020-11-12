package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import com.medical.sterismart.model.DataPackageModel
import kotlinx.android.synthetic.main.item_package_create.view.*

class InstrumentAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<InstrumentAdapter.PackageAdapterViewHolder>() {

    var instrumentsItems: MutableList<DataPackageModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_package_create, parent, false)
        return PackageAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return instrumentsItems.count()
    }

    override fun onBindViewHolder(holder: PackageAdapterViewHolder, position: Int) {
        val packageItemItem = instrumentsItems[position]
        holder.itemView.instrument_name.text = packageItemItem.instrumentName

        when {
            instrumentsItems[position].isSelected -> {
                instrumentsItems[position].isSelected = true
            }
            else -> {
                instrumentsItems[position].isSelected = false
            }
        }

        holder.itemView.instrument_name.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> {
                    instrumentsItems[position].isSelected = true
                }
                else -> {
                    instrumentsItems[position].isSelected = false
                }
            }
        }
    }

    class PackageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}