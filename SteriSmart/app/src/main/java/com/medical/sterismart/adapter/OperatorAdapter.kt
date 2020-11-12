package com.medical.sterismart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medical.data.tables.UsersModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_operator.view.*

class OperatorAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<OperatorAdapter.OperatorAdapterViewHolder>() {

    var userItems: MutableList<UsersModel> = ArrayList()

    var onItemSelectListener: OnOperatorItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_operator, parent, false)
        return OperatorAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userItems.size
    }

    override fun onBindViewHolder(holder: OperatorAdapterViewHolder, position: Int) {
        val userItem = userItems[position]

        holder.itemView.f_name.text = userItem.firstName
        holder.itemView.l_name.text = userItem.lastName

        holder.itemView.edit.setOnClickListener {
            onItemSelectListener?.onItemSelected(userItem, false)
        }

        holder.itemView.delete.setOnClickListener {
            onItemSelectListener?.onItemSelected(userItem, true)
        }
    }

    class OperatorAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnOperatorItemSelectListener {
        fun onItemSelected(usersModel: UsersModel, isDeleted: Boolean)
    }
}