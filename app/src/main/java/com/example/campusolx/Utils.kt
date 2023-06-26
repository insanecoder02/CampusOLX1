package com.example.campusolx

import android.content.Context
import android.os.Message
import android.widget.Toast

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
}