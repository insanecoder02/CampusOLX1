package com.example.campusolx.activites

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import com.example.campusolx.R

// Define the LoginLoader class, which is a custom dialog
class LoginLoader : Dialog {

    // Primary constructor that takes a context as a parameter
    constructor(context: Context) : super(context)

    // Secondary constructor that takes a context and a theme resource ID as parameters
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view of the dialog to the layout defined in "activity_jarvis_loader.xml"
        setContentView(R.layout.activity_jarvis_loader)

        // Set the dimensions of the dialog window to match the parent's dimensions
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        // Set the background of the dialog window to a white transparent color
        window?.setBackgroundDrawableResource(R.color.white_transparent)
    }
}
