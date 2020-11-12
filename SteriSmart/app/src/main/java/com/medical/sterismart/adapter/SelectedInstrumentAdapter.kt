package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import com.medical.sterismart.model.DataPackageModel
import kotlinx.android.synthetic.main.item_selected_instrument.view.*

class SelectedInstrumentAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<SelectedInstrumentAdapter.PackageAdapterViewHolder>() {

    var instrumentsItems: MutableList<DataPackageModel> = ArrayList()
    var onItemDeleteListener: OnItemDeleteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selected_instrument, parent, false)
        return PackageAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return instrumentsItems.count()
    }

    override fun onBindViewHolder(holder: PackageAdapterViewHolder, position: Int) {
        val instrumentItem = instrumentsItems[position]
        holder.itemView.instrument_name.text = instrumentItem.instrumentName
        holder.itemView.delete.setOnClickListener {
            onItemDeleteListener?.onItemDelete(instrumentItem)
        }
    }

    class PackageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemDeleteListener {
        fun onItemDelete(item: DataPackageModel)
    }
}