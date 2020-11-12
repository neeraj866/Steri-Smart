package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_manufacturer.view.*

class ModelAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<ModelAdapter.TypeAdapterViewHolder>() {

    var models: List<String> = ArrayList()
    var onModelSelected: OnModelSelected? = null
    var images: List<Int> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manufacturer, parent, false)
        return TypeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
        holder.itemView.autoclave_manufacturer.text = models[position]
        holder.itemView.autoclave_image.setImageDrawable(
            context!!.getDrawable(
                images[position]
            )
        )
        holder.itemView.setOnClickListener {
            onModelSelected?.selectedModel(models[position], images[position])
        }
    }

    class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnModelSelected {
        fun selectedModel(modelText: String, image: Int)
    }
}