package com.mediadocena.clubdeportivo.utils
import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class SnackbarUtils {
    companion object {
        fun showCustomSnackbar(activity: Activity, message: String, type: String): Snackbar {
            val snackbar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
            val snackbarView = snackbar.view
            val snackbarText =
                snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

            when (type) {
                "error" -> {
                    snackbar.setBackgroundTint(Color.parseColor("#DC143C"))
                    snackbarText.setTextColor(Color.parseColor("#FFFFFF"))
                }

                "success" -> {
                    snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
                    snackbarText.setTextColor(Color.parseColor("#FFFFFF"))
                }

                "warning" -> {
                    snackbar.setBackgroundTint(Color.parseColor("#FFA500"))
                    snackbarText.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }

            return snackbar
        }
    }
}