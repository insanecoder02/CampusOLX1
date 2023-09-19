package com.example.campusolx.utils

import android.content.Context
import com.example.campusolx.activites.LoginLoader

// Utility class for managing loading dialogs
open class LoadingUtils {

    companion object {
        // A reference to the loading dialog
        private var loginLoader: LoginLoader? = null

        // Function to show a loading dialog
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog() // Make sure to hide any existing dialog before showing a new one
            if (context != null) {
                try {
                    loginLoader = LoginLoader(context)
                    loginLoader?.let { loader ->
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
                loginLoader?.let { loader ->
                    if (loader.isShowing) {
                        loader.dismiss()
                    }
                    loginLoader = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
