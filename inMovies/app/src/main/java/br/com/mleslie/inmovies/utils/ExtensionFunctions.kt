package br.com.mleslie.inmovies.utils

import android.content.Context
import android.widget.Toast

class ExtensionFunctions {
    fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
}