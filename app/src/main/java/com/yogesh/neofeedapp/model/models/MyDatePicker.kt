package com.yogesh.neofeedapp.model.models

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

open class MyDatePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mCalendar = Calendar.getInstance()
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH]
        val dayOfMonth = mCalendar[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(requireContext(), requireParentFragment() as OnDateSetListener?, year, month, dayOfMonth)
    }
}