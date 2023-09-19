package com.example.campusolx.utils

import android.content.Context
import com.example.campusolx.activites.LoginLoader

// Utility class for managing the loading dialog during registration
open class RegisterLoadingUtils {

    companion object {
        // A reference to the loading dialog
        private var RegisterLoader: LoginLoader? = null

        // Function to show the loading dialog
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog() // Make sure to hide any existing dialog before showing a new one
            if (context != null) {
                try {
                    RegisterLoader = LoginLoader(context)
                    RegisterLoader?.let { loader ->
                        loader.setCanceledOnTouchOutside(true)
                        loader.setCancelable(isCancelable)
                        loader.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Function to hide the loading dialog
        fun hideDialog() {
            try {
                RegisterLoader?.let { loader ->
                    if (loader.isShowing) {
                        loader.dismiss()
                    }
                    RegisterLoader = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
