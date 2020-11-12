package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.AutoclaveModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_manufacturer.view.*

class AutoclaveModelAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<AutoclaveModelAdapter.TypeAdapterViewHolder>() {

    var autoclaveModel: MutableList<AutoclaveModel> = ArrayList()
    var onModelSelected: OnModelSelected? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manufacturer, parent, false)
        return TypeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return autoclaveModel.count()
    }

    override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
        holder.itemView.autoclave_image.setImageDrawable(
            context!!.getDrawable(
                autoclaveModel[position].image
            )
        )
        holder.itemView.autoclave_manufacturer.text = autoclaveModel[position].name
        holder.itemView.setOnClickListener {
            onModelSelected?.selectedManufacturer(autoclaveModel[position])
        }
    }

    class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnModelSelected {
        fun selectedManufacturer(autoclaveModel: AutoclaveModel)
    }
}