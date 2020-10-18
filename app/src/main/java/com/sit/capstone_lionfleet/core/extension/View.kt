package com.sit.capstone_lionfleet.core.extension

import android.content.Context
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.internal.ViewUtils
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
fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}
fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

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
