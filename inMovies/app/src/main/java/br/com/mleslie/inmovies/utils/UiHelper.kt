package br.com.mleslie.inmovies.utils

import android.view.View
import android.widget.ProgressBar

object UiHelper {
    fun showProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.GONE
    }
}