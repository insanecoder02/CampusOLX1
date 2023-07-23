package com.example.campusolx.utils

import android.content.Context
import com.example.campusolx.activites.AdLoader
import com.example.campusolx.fragments.HomeFragment


open class AdLoader {

    companion object {
        private var AdLoader: AdLoader? = null

        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog() // Make sure to hide any existing dialog before showing a new one
            if (context != null) {
                try {
                    AdLoader = AdLoader(context)
                    AdLoader?.let { loader ->
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
                AdLoader?.let { loader ->
                    if (loader.isShowing) {
                        loader.dismiss()
                    }
                    AdLoader = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}