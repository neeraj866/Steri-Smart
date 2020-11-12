package com.medical.sterismart.custom

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.model.DataPackageModel
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton


object Utility {
    private var dialog: Dialog? = null

    @Singleton
    fun showProgressDialog(activity: Context) {
        dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = dialog!!.window?.attributes
        lp?.dimAmount = 0.5f

        dialog!!.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog!!.setContentView(com.medical.sterismart.R.layout.progress_layout)
        dialog!!.show()
    }

    fun dismissDialog() {
        when {
            dialog != null && dialog!!.isShowing -> {
                dialog!!.dismiss()
            }
        }
    }

    fun showNotification(context: FragmentActivity, content: String) {
        val channelID = "com.medical.sterismart"

        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notification = Notification.Builder(
            context,
            channelID
        )
            .setContentTitle("SteriSmart")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()

        notificationManager.notify(101, notification)
    }

    @Singleton
    fun chooseDate(context: Context, onDateSelectListener: OnDateSelectListener) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
//                calendar[year, month] = day
                calendar.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())

                val dateString: String = dateFormat.format(calendar.time)
                onDateSelectListener.selectedDate(dateString)
            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    interface OnDateSelectListener {
        fun selectedDate(dateString: String)
    }

    @Singleton
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @Singleton
    fun convertJsonToString(instruments: List<DataPackageModel>): String {
        return Gson().toJson(instruments)
    }

    @Singleton
    fun convertStringToJson(instruments: String): MutableList<DataPackageModel> {
        val listType: Type = object : TypeToken<List<DataPackageModel?>?>() {}.type
        return Gson().fromJson(instruments, listType)
    }

    @Singleton
    fun convertPackageStringToJson(packages: String): MutableList<SterilizationPackagesModel> {
        val listType: Type = object : TypeToken<List<SterilizationPackagesModel?>?>() {}.type
        return Gson().fromJson(packages, listType)
    }

    @Singleton
    fun getCurrentDate(): String {
        val pattern = "EEEE, MMMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    @Singleton
    fun getCurrentTime(): String {
        val pattern = "HH:mm"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    @Singleton
    fun covertLogDate(date: String): String {
        val splitDate = date.split(" ")
        val calendar = Calendar.getInstance()
        val year = splitDate[2].toInt()
        val month = monthCount(splitDate[0])
        val day = splitDate[1].toInt()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())

        return dateFormat.format(calendar.time)
    }


    fun bitmapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return android.util.Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    fun stringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte =
                Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    private fun monthCount(month: String): Int {
        var monthCount = -1
        when {
            month.contains("jan", true) -> {
                monthCount = 0
            }
            month.contains("feb", true) -> {
                monthCount = 1
            }
            month.contains("mar", true) -> {
                monthCount = 2
            }
            month.contains("apr", true) -> {
                monthCount = 3
            }
            month.contains("may", true) -> {
                monthCount = 4
            }
            month.contains("jun", true) -> {
                monthCount = 5
            }
            month.contains("jul", true) -> {
                monthCount = 6
            }
            month.contains("aug", true) -> {
                monthCount = 7
            }
            month.contains("sep", true) -> {
                monthCount = 8
            }
            month.contains("oct", true) -> {
                monthCount = 9
            }
            month.contains("nov", true) -> {
                monthCount = 10
            }
            month.contains("dec", true) -> {
                monthCount = 11
            }
        }
        return monthCount
    }

    @Singleton
    fun convertDate(date: String): Long {
        val convertedDate: Date? =
            SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).parse(date)
        val pattern = "dd-MM-yyyy"
        return dateToTimestamp(convertedDate!!)!!
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    fun initializeDialog(activity: Context): Dialog {
        val dialog = Dialog(activity, R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        val lp = dialog.window?.attributes
        lp?.dimAmount = 0.5f
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        return dialog
    }

    fun saveLanguage(context: Context, language: String) {
        val sharedPreference =
            context.getSharedPreferences("STERI_TOUCH_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("language", language)
        editor.commit()
    }

    fun getLanguage(context: Context): String? {
        val sharedPreference =
            context.getSharedPreferences("STERI_TOUCH_PREFERENCES", Context.MODE_PRIVATE)
        return sharedPreference.getString("language", "en")
    }

    fun saveKey(context: Context, key: String) {
        val sharedPreference =
            context.getSharedPreferences("STERI_TOUCH_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("login_key", key)
        editor.commit()
    }

    fun getKey(context: Context): String? {
        val sharedPreference =
            context.getSharedPreferences("STERI_TOUCH_PREFERENCES", Context.MODE_PRIVATE)
        return sharedPreference.getString("login_key", "")
    }

    @Singleton
    fun changeLanguage(language: String, activity: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
    }
}