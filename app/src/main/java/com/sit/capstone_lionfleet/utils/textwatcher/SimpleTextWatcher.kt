package com.continental.cityfleet.utils.textwatcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

@Suppress("EmptyFunctionBlock")
open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(input: Editable?) {}

    override fun onTextChanged(input: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun beforeTextChanged(input: CharSequence?, start: Int, count: Int, after: Int) {}
}


