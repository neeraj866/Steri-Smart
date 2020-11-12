package com.medical.sterismart.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import kotlinx.android.synthetic.main.item_sterilization_label.view.*

class SterilizationLabelAdapter(val context: FragmentActivity?) :
        RecyclerView.Adapter<SterilizationLabelAdapter.TypeAdapterViewHolder>() {

    var packages: MutableList<SterilizationPackagesModel> = ArrayList()
    var onPackageCountChangeListener: OnPackageCountChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sterilization_label, parent, false)
        return TypeAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packages.count()
    }

    override fun onBindViewHolder(holder: TypeAdapterViewHolder, position: Int) {
        holder.itemView.code.text = packages[position].id.toString()
        holder.itemView.package_name.text = packages[position].packageName
        holder.itemView.package_type.text = packages[position].packageType
        val dateTime = "${Utility.getCurrentDate()} ${Utility.getCurrentTime()}"
        holder.itemView.date.text = dateTime

        val ob = BitmapDrawable(context!!.resources, createBitmap(packages[position].id.toString()))

//        holder.itemView.qr_code.setImageBitmap(createBitmap(packages[position].id.toString()))
        holder.itemView.qr_code.background = ob
    }

    fun createBitmap(code: String): Bitmap {
        val qrCodeGenerator = QRCodeWriter()
        val qrCode = qrCodeGenerator.encode(
                code,
                BarcodeFormat.QR_CODE,
                150,
                150
        )
        val height: Int = qrCode.height
        val width: Int = qrCode.width
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (qrCode.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    class TypeAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPackageCountChangeListener {
        fun packageCountChange(count: Int, position: Int)
        fun onTypeChanged(type: String, position: Int)
    }
}