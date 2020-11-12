package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import com.medical.sterismart.model.DataPackageModel
import kotlinx.android.synthetic.main.item_package.view.*

class ViewInstrumentAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<ViewInstrumentAdapter.PackageAdapterViewHolder>() {

    var instrumentItems: List<DataPackageModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_package_create, parent, false)
        return PackageAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return instrumentItems.count()
    }

    override fun onBindViewHolder(holder: PackageAdapterViewHolder, position: Int) {
        val instrumentItem = instrumentItems[position].instrumentName
        holder.itemView.package_name.text = instrumentItem
    }

    class PackageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}