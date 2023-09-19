package com.example.campusolx.activites

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import com.example.campusolx.R

// Define a custom dialog class named RegisterLoader
class RegisterLoader : Dialog {

    // Constructor without a theme
    constructor(context: Context) : super(context)

    // Default constructor with a theme resource ID
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    // Override the onCreate method to initialize the dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view for the custom dialog using the defined layout
        setContentView(R.layout.activity_register_loader)

        // Set the layout parameters for the dialog window to match parent's width and height, making it fullscreen
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        // Set the background resource for the dialog window to a white transparent color
        window?.setBackgroundDrawableResource(R.color.white_transparent)
    }
}
