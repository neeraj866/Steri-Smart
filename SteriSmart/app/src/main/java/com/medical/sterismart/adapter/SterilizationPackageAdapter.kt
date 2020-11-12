package com.medical.sterismart.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.PackagesModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_sterilization_package.view.*

class SterilizationPackageAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<SterilizationPackageAdapter.TypeAdapterViewHolder>() {

    var packages: MutableList<PackagesModel> = ArrayList()
    var onPackageCountChangeListener: OnPackageCountChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sterilization_package, parent, false)
        return TypeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packages.count()
    }

    override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
        holder.itemView.package_name.text = packages[position].packageName
        holder.itemView.package_count.text = packages[position].packageCount.toString()
        holder.itemView.plus.setOnClickListener {
            val count = holder.itemView.package_count.text.toString().toInt() + 1
            onPackageCountChangeListener?.packageCountChange(count, position)
            holder.itemView.package_count.text = count.toString()
        }

        holder.itemView.wrapped.typeface = Typeface.DEFAULT
        holder.itemView.unwrapped.typeface = Typeface.DEFAULT
        holder.itemView.cassette.typeface = Typeface.DEFAULT

        holder.itemView.wrapped.setTextColor(
            Color.BLACK
        )
        holder.itemView.unwrapped.setTextColor(
            Color.BLACK
        )
        holder.itemView.cassette.setTextColor(
            Color.BLACK
        )
        when (packages[position].packageType) {
            "Wrapped" -> {
                holder.itemView.wrapped.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.dark_red
                    )
                )
                holder.itemView.wrapped.typeface = Typeface.DEFAULT_BOLD
            }
            "Unwrapped" -> {
                holder.itemView.unwrapped.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.dark_red
                    )
                )
                holder.itemView.unwrapped.typeface = Typeface.DEFAULT_BOLD
            }
            "Cassette" -> {
                holder.itemView.cassette.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.dark_red
                    )
                )
                holder.itemView.cassette.typeface = Typeface.DEFAULT_BOLD
            }
        }

        holder.itemView.wrapped.setOnClickListener {
            onPackageCountChangeListener?.onTypeChanged(
                "Wrapped",
                position
            )
        }

        holder.itemView.unwrapped.setOnClickListener {
            onPackageCountChangeListener?.onTypeChanged(
                "Unwrapped",
                position
            )

        }

        holder.itemView.cassette.setOnClickListener {
            onPackageCountChangeListener?.onTypeChanged(
                "Cassette",
                position
            )

        }

        holder.itemView.minus.setOnClickListener {
            var count = holder.itemView.package_count.text.toString().toInt()
            when {
                count != 0 -> {
                    count -= 1
                    onPackageCountChangeListener?.packageCountChange(count, position)
                }
            }
        }
    }

    class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPackageCountChangeListener {
        fun packageCountChange(count: Int, position: Int)
        fun onTypeChanged(type: String, position: Int)
    }
}