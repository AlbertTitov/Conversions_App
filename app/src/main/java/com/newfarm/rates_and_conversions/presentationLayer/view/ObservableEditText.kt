package com.newfarm.rates_and_conversions.presentationLayer.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import io.reactivex.subjects.PublishSubject


class ObservableEditText : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet) : super (context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    var isBase = false
        set(value) {
            field = value
            if (value) {
                requestFocus()
                //showTheKeyboard(this.context, this)
            }
        }

    var selectionCallback: () -> Unit = {}
    var baseCurrencyChangedCallback: PublishSubject<String>? = null

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        if (!this.text.isNullOrEmpty()) {
            this.setSelection(this.text!!.length)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (!isBase) {
            selectionCallback()
        }
        return super.dispatchTouchEvent(event)
    }

    private fun init() {
       this.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
            if (isBase) {
                baseCurrencyChangedCallback?.let {
                    baseCurrencyChangedCallback!!.onNext(sequence.toString())
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        selectionCallback = {}
        baseCurrencyChangedCallback = null
    }

    private fun showTheKeyboard(context: Context, editText: EditText?) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}