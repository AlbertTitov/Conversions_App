package com.newfarm.rates_and_conversions.presentationLayer.bindingAdapters

import android.widget.ImageView
import androidx.databinding.*
import androidx.recyclerview.widget.RecyclerView
import com.newfarm.rates_and_conversions.helpers.formatCurrency
import com.newfarm.rates_and_conversions.presentationLayer.view.adapters.BaseRealmAdapter
import com.newfarm.rates_and_conversions.presentationLayer.view.ObservableEditText
import io.reactivex.subjects.PublishSubject

object BindingAdaptersCollection {

    @BindingAdapter("adapter")
    @JvmStatic fun setAdapter(view: RecyclerView, adapter: BaseRealmAdapter<*>?) {
        adapter?.let {
            view.adapter = adapter
        }
    }

    @BindingAdapter("resource")
    @JvmStatic fun setResFromDrawable(view: ImageView, sourceId: Int) {
        view.setImageResource(sourceId)
    }

    @BindingAdapter(value = ["isBase", "baseAmount", "currencyRate",
        "selectionCallback", "baseCurrencyChanged"], requireAll = true)
    @JvmStatic fun setupCurrencyEditText(view: ObservableEditText, isBase: Boolean, baseAmount: Double,
                                         currencyRate: Double, selectionCallback: () -> Unit,
                                         baseCurrencyChangedCallback: PublishSubject<String>) {
        view.isBase = isBase
        view.selectionCallback = selectionCallback
        if (view.baseCurrencyChangedCallback == null) {
            view.baseCurrencyChangedCallback = baseCurrencyChangedCallback
        }

        if (isBase) {
            view.setText(formatCurrency(baseAmount))
        }
        else {
            view.setText(formatCurrency(currencyRate * baseAmount))
        }
    }
}