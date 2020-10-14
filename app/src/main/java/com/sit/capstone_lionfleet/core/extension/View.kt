package com.sit.capstone_lionfleet.core.extension

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.sit.capstone_lionfleet.R

private const val DELAY_IN_MILLIS = 100L

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.enable(enabled : Boolean){
    isEnabled = enabled
}
fun View.invisibleIf(condition: Boolean) {
    if (condition) {
        this.visibility = View.INVISIBLE
    } else {
        show()
    }
}

fun View.showIf(condition: Boolean) {
    if (condition) {
        show()
    } else {
        hide()
    }
}



fun View.showKeyboard() {
    val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    Handler().postDelayed({
        this.requestFocus()
        inputMethodManager.showSoftInput(
            this,
            InputMethodManager.SHOW_IMPLICIT
        )
    }, DELAY_IN_MILLIS)
}

fun View.hideKeyboard() {
    if (windowToken == null) {
        return
    }
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    this.clearFocus()
}
