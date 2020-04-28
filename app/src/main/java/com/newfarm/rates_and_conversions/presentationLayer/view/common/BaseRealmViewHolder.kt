package com.newfarm.rates_and_conversions.presentationLayer.view.common

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseRealmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var binding: ViewDataBinding = DataBindingUtil.bind(itemView)!!

    fun getBinding(): ViewDataBinding {
        return binding
    }
}