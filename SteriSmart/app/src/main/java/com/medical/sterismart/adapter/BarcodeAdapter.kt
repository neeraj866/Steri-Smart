package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_barcode.view.*

class BarcodeAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<BarcodeAdapter.BarcodeAdapterViewHolder>() {

    var barcodeItems: List<String> = ArrayList()
    var onBarcodeDeleteListener: OnBarcodeDeleteListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_barcode, parent, false)
        return BarcodeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return barcodeItems.count()
    }

    override fun onBindViewHolder(holder: BarcodeAdapterViewHolder, position: Int) {
        holder.itemView.barcode_text.text = barcodeItems[position]
        holder.itemView.delete.setOnClickListener {
            onBarcodeDeleteListener?.deleteBarcode(barcodeItems[position])
        }
    }

    class BarcodeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnBarcodeDeleteListener {
        fun deleteBarcode(barcode: String)
    }
}