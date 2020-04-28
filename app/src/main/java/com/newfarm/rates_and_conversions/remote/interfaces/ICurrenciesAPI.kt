package com.newfarm.rates_and_conversions.remote.interfaces

import com.newfarm.rates_and_conversions.remote.response.CurrencyRatesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ICurrenciesAPI {

    @GET("android/latest")
    fun fetchCurrencyRates(@Query("base") baseCurrency: String): Observable<CurrencyRatesResponse>
}