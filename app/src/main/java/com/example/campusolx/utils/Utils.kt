package com.example.campusolx.utils

import android.content.Context
import android.widget.Toast
import com.example.campusolx.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

object Utils {

    val categories = arrayOf(
        "Coolers",
        "Electronics",
        "Furniture",
        "Books",
        "Sports",
        "Nitish Bharat",
        "Fashion",
        "Girlfriends"
    )
    val categoryIcons = arrayOf(
        R.drawable.ic_phone,
        R.drawable.baseline_electrical_services_24,
        R.drawable.baseline_bed_24,
        R.drawable.ic_book,
        R.drawable.baseline_sports_handball_24,
        R.drawable.ic_person,
        R.drawable.ic_chat,
        R.drawable.baseline_girl_24
    )

    fun getTimestamp() : Long{
        return System.currentTimeMillis()
    }

    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun formatTimestampDate(timestamp : Long) : String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        return android.text.format.DateFormat.format("dd/MM/yyyy",calendar).toString()
    }
}