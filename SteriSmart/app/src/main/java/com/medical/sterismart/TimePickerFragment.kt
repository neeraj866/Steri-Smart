package com.medical.sterismart

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var onTimeChangeListener: OnTimeChangeListener? = null

    fun initializeListener(onTimeChangeListener: OnTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(activity))
    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val time = "$hourOfDay:$minute"
        onTimeChangeListener?.onTimeChanges(time)
    }

    interface OnTimeChangeListener {
        fun onTimeChanges(selectedTime: String)
    }
}