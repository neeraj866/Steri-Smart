package com.medical.sterismart.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import kotlinx.android.synthetic.main.item_package.view.*
import java.util.*
import kotlin.collections.ArrayList


class PackageAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    var packageItems: MutableList<PackagesModel> = ArrayList()
    var onPackageSelectedListener: OnPackageSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_package, parent, false)
        return PackageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packageItems.count()
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packageItems[position]
        holder.itemView.package_name.text = packageItem.packageName.toUpperCase(Locale.ROOT)
        var instrumentsCount = 0
        when {
            !TextUtils.isEmpty(packageItem.instruments) -> {
                val instruments = Utility.convertStringToJson(packageItem.instruments)
                instrumentsCount = instruments.size
            }
        }
        val instruments = "$instrumentsCount ${context?.getString(R.string.instruments_in_package)}"
        holder.itemView.instrument_count.text = instruments

        holder.itemView.edit.setOnClickListener {
            onPackageSelectedListener?.editPackage(packageItem)
        }
        holder.itemView.delete.setOnClickListener {
            onPackageSelectedListener?.deletePackage(packageItem)
        }
    }


    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    interface OnPackageSelectedListener {
        fun deletePackage(packagesModel: PackagesModel)
        fun editPackage(packagesModel: PackagesModel)
    }
}