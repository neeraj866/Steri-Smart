package com.medical.sterismart.custom

import android.R
import android.app.Dialog
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_data_list.*
import kotlinx.android.synthetic.main.item_model.view.*

object DialogUtils {

    fun showListDialog(context: Context,
        title: String,
        list: List<String>,
        onItemSelected: OnItemSelected) {
        val dataDialog = Dialog(context, R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = dataDialog.window?.attributes
        lp?.dimAmount = 0.5f

        dataDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dataDialog.setContentView(com.medical.sterismart.R.layout.dialog_data_list)

        dataDialog.dialog_title.text = title

        val modelAdapter = ListAdapter(context)
        dataDialog.data_list!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        modelAdapter.dataList = list
        dataDialog.data_list!!.adapter = modelAdapter
        modelAdapter.onItemSelected = object : OnItemSelected {
            override fun selectedItem(item: String) {
                onItemSelected.selectedItem(item)
                dataDialog.dismiss()
            }
        }

        dataDialog.show()

        dataDialog.cancel!!.setOnClickListener {
            dataDialog.dismiss()
        }
    }

    interface OnItemSelected {
        fun selectedItem(item: String)
    }

    class ListAdapter(val context: Context?) :
        RecyclerView.Adapter<ListAdapter.TypeAdapterViewHolder>() {

        var dataList: List<String> = ArrayList()
        var onItemSelected: OnItemSelected? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(com.medical.sterismart.R.layout.item_model, parent, false)
            return TypeAdapterViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataList.count()
        }

        override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
            holder.itemView.autoclave_model.text = dataList[position]
            holder.itemView.setOnClickListener {
                onItemSelected?.selectedItem(dataList[position])
            }
        }

        class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}

