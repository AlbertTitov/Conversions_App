package com.newfarm.rates_and_conversions.presentationLayer.view.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BaseRecyclerView : RecyclerView {

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        this.layoutManager = LinearLayoutManager(this.context)
    }

    var isListClickable = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (isListClickable) {
            super.dispatchTouchEvent(ev)
        }
        else {
            true
        }
    }
}