package com.medical.sterismart.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_package.view.*

class ViewPackageAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<ViewPackageAdapter.PackageAdapterViewHolder>() {

    var packageItems: MutableList<PackagesModel> = ArrayList()
    var onItemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_package_create, parent, false)
        return PackageAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packageItems.count()
    }

    override fun onBindViewHolder(holder: PackageAdapterViewHolder, position: Int) {
        val packageItemItem = packageItems[position]
        holder.itemView.package_name.text = packageItemItem.packageName
        if (packageItemItem.isSelected) {
            holder.itemView.package_name.setBackgroundColor(ContextCompat.getColor(context!!,
                R.color.light_grey))
        } else {
            holder.itemView.package_name.setBackgroundColor((Color.TRANSPARENT))
        }
        holder.itemView.package_name.setOnClickListener {
            onItemSelectListener?.onItemSelected(packageItems[position])
            for (i in packageItems.indices) {
                when {
                    packageItems[i] != packageItems[position] -> {
                        packageItems[i].isSelected = false
                    }
                }
            }
            packageItems[position].isSelected = true
            notifyDataSetChanged()
        }
    }

    class PackageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemSelectListener {
        fun onItemSelected(packagesModel: PackagesModel)
    }
}