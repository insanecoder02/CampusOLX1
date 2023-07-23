package com.example.campusolx.activites

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import com.example.campusolx.R

class RegisterLoader : Dialog {

    constructor(context: Context) : super(context)

    // Default constructor
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_loader)

        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.white_transparent)
    }
}
