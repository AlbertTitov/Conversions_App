package com.newfarm.rates_and_conversions.remote

import com.newfarm.rates_and_conversions.remote.common.ServerAPI
import com.newfarm.rates_and_conversions.remote.interfaces.ICurrenciesAPI
import com.newfarm.rates_and_conversions.remote.response.CurrencyRatesResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CurrenciesAPI : ServerAPI<ICurrenciesAPI>(ICurrenciesAPI::class)  {

    fun fetchCurrencyRates(baseCurrency: String) : Observable<CurrencyRatesResponse> {
        return get().fetchCurrencyRates(baseCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}