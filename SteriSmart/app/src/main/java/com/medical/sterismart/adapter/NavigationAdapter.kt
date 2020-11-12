package com.medical.sterismart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medical.sterismart.model.NavigationModel
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.item_navigation.view.*


class NavigationAdapter(val context: Context) :
    RecyclerView.Adapter<NavigationAdapter.NavigationAdapterViewHolder>() {

    var navItems: List<NavigationModel> = ArrayList()
    var onMenuSelected: OnMenuSelectedListener? = null

    interface OnMenuSelectedListener {
        fun onMenuSelected(menuName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationAdapterViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_navigation, parent, false)
        return NavigationAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return navItems.count()
    }

    override fun onBindViewHolder(holder: NavigationAdapterViewHolder, position: Int) {
        val navItem = navItems[position]
        holder.itemView.icon.setImageDrawable(context.getDrawable(navItem.icon))
        holder.itemView.name.text = navItem.name
        holder.itemView.setOnClickListener { onMenuSelected?.onMenuSelected(navItem.name) }
    }

    class NavigationAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}