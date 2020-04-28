package com.newfarm.rates_and_conversions.presentationLayer.view.activity

import com.newfarm.rates_and_conversions.R
import com.newfarm.rates_and_conversions.databinding.CurrencyConversionsActivityBinding
import com.newfarm.rates_and_conversions.presentationLayer.view.activity.common.DataBindingActivity
import com.newfarm.rates_and_conversions.presentationLayer.viewModel.CurrencyConversionsViewModel
import io.reactivex.rxkotlin.addTo

class CurrencyConversionsActivity : DataBindingActivity<CurrencyConversionsActivityBinding, CurrencyConversionsViewModel>() {

    override fun getLayoutResourceId(): Int {
        return R.layout.currency_conversions_activity
    }

    override fun onBinding(binding: CurrencyConversionsActivityBinding) {
        binding.viewModel = this.viewModel

        /*** Показываем ProgressDialog при первом запуске и пустом списке ***/
        viewModel.showLoading.subscribe {
            if (it) {
                showProgressDialog()
            }
            else {
                hideProgressDialog()
            }
        }.addTo(disposable)
    }

    override fun onInit() {
        super.onInit()
        this.viewModel = CurrencyConversionsViewModel()
    }
}
