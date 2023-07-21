package com.example.campusolx.utils

import android.content.Context
import com.example.campusolx.activites.LoginLoader

open class LoadingUtils {

    companion object {
        private var loginLoader: LoginLoader? = null

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

