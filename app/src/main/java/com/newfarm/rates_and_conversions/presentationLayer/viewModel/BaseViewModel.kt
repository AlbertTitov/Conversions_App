package com.newfarm.rates_and_conversions.presentationLayer.viewModel

import com.newfarm.rates_and_conversions.extentions.executeTransactionSafe
import com.newfarm.rates_and_conversions.presentationLayer.model.Currency
import com.newfarm.rates_and_conversions.presentationLayer.viewModel.common.RootViewModel
import com.newfarm.rates_and_conversions.remote.CurrenciesAPI
import io.reactivex.Observable
import io.realm.RealmList

open class BaseViewModel : RootViewModel() {

    companion object {
        const val DEFAULT_BASE_CURRENCY = "EUR"
        const val DEFAULT_CURRENCY_QUANTITY = 100.toDouble()
        const val SYNC_INTERVAL_MILLIS = 960L
    }

    private val mCurrenciesAPI = CurrenciesAPI()

    protected fun requestCurrencyRates(baseCurrency: String): Observable<Boolean> {
        return mCurrenciesAPI.fetchCurrencyRates(baseCurrency)
            .map { response ->

                val currencies = RealmList<Currency>()
                response.rates?.forEach { entry ->

                    val currency = Currency()
                    currency.name = entry.key
                    currency.rate = entry.value
                    currencies.add(currency)
                }

                currencies
            }
            .map { response ->
                realm.executeTransactionSafe {
                    /*** Асинхронно запрошенные данные могут перезаписать выбранную базовую валюту (значение isBase перезапишется дефолтным знач-ем "false"),
                     * т. к. транзакция обновления списка валют может произойти позже транзакции
                     * "назначения" выбранной валюты в качестве базовой.
                     * Поэтому исключаем базовую валюту из списка синхронизируемых; к тому же, нам не нужен "rate" выбранной валюты, а важно только её количество ***/
                    val baseCurrencySelected = realm.where(Currency::class.java).equalTo("isBase", true).findFirst()
                    realm.copyToRealmOrUpdate(response.filter { it != baseCurrencySelected })
                }
                true
            }
    }
}