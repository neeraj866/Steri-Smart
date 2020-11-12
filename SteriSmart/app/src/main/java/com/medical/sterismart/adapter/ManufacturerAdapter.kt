package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_manufacturer.view.*

class ManufacturerAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<ManufacturerAdapter.TypeAdapterViewHolder>() {

    var manufacturers: List<String> = ArrayList()
    var onManufacturerSelected: OnManufacturerSelected? = null

    private val autoclaveManufacturers = arrayOf(
        R.drawable.flight_dental,
        R.drawable.masteri,
        R.drawable.midmark,
        R.drawable.ritter,
        R.drawable.scican,
        R.drawable.tuttnauer,
        R.drawable.w_h_lisa,
        R.drawable.other_autoclave
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manufacturer, parent, false)
        return TypeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return manufacturers.count()
    }

    override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
        holder.itemView.autoclave_image.setImageDrawable(
            context!!.getDrawable(
                autoclaveManufacturers[position]
            )
        )
        holder.itemView.autoclave_manufacturer.text = manufacturers[position]
        holder.itemView.setOnClickListener {
            onManufacturerSelected?.selectedManufacturer(manufacturers[position])
        }
    }

    class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnManufacturerSelected {
        fun selectedManufacturer(manufacturerText: String)
    }
}