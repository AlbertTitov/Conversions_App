package com.newfarm.rates_and_conversions.presentationLayer.viewModel

import android.os.Handler
import androidx.databinding.Bindable
import com.newfarm.rates_and_conversions.BR
import com.newfarm.rates_and_conversions.R
import com.newfarm.rates_and_conversions.extentions.executeTransactionSafe
import com.newfarm.rates_and_conversions.presentationLayer.model.Currency
import com.newfarm.rates_and_conversions.presentationLayer.view.adapters.BaseRealmAdapter
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.realm.RealmResults
import io.realm.Sort

class CurrencyConversionsViewModel : BaseViewModel() {

    val showLoading: PublishSubject<Boolean> = PublishSubject.create()

    @get:Bindable
    var currencyRates: RealmResults<Currency>? = null
        get() {
            if (field == null) {
                field = realm.where(Currency::class.java).findAll().sort("isBase", Sort.DESCENDING)
            }
            return field
        }
        private set(value) {
            field = value
            notifyPropertyChanged(BR.currencyRates)
        }

    @get:Bindable
    var baseCurrency: Currency? = null
        get() {
            if (field == null) {
                field = currencyRates!!.firstOrNull { it.isBase }
            }
            return field
        }
        private set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.baseCurrency)
            }
        }

    @get:Bindable
    var baseCurrencyQuantity: Double? = null
        get() {
            if (field == null && baseCurrency != null) {
                field = DEFAULT_CURRENCY_QUANTITY
            }
            return field
        }
        private set(value) {
            if (field != value && value != null) {
                field = value
                notifyPropertyChanged(BR.baseCurrencyQuantity)
            }
        }

    val setCurrencyQuantity: PublishSubject<String> = PublishSubject.create()

    @get:Bindable
    var currencyConversionsAdapter: BaseRealmAdapter<Currency>? = null
        get() {
            if (field == null) {
                val adapter = BaseRealmAdapter(this, R.layout.currencies_list_item, currencyRates!!,
                    automaticUpdate = true,
                    updateOnModification = true
                )
                adapter.onItemClick.subscribe { model ->

                    resetBaseCurrency(model)

                }.addTo(disposable)
                field = adapter
            }
            return field
        }

    fun resetBaseCurrency(model: Currency) {
        if (model.name != baseCurrency?.name) {
            /***
             * Данные могут перезаписаться rate'ами предыдущей базовой валюты, в течение 1 сек будем видеть невалидные данные.
             * Поэтому отменяем подписку на предыдущий запрос***/
            networkDisposable.clear()

            showLoading.onNext(true)

            realm.executeTransactionSafe {
                currencyRates!!.firstOrNull { it.isBase }?.isBase = false
                currencyRates!!.first { it.name == model.name }.isBase = true
            }
            baseCurrencyQuantity = baseCurrencyQuantity!! * model.rate!!
            baseCurrency = model
            currencyRates = null
            currencyConversionsAdapter = null
            notifyPropertyChanged(BR.currencyConversionsAdapter)
        }
    }

    private fun getCurrencyRates() {
        if (currencyRates!!.isEmpty()) {
            showLoading.onNext(true)
        }
        requestCurrencyRates(baseCurrency?.name ?: DEFAULT_BASE_CURRENCY)
            .subscribe({
                showLoading.onNext(false)
                resetValues()
            },{
                showLoading.onNext(false)
                error.onNext(it)
            })
            .addTo(networkDisposable)
    }

    private fun resetValues() {
        currencyRates = null
        currencyConversionsAdapter = null

        notifyPropertyChanged(BR.baseCurrency)

        /*** Если запросили данные по сети и в списке валют нет валюты с признаком "базовый", то
         *   в качестве базовой валюты принимаем первую из списка ***/
        if (baseCurrency == null) {
            baseCurrency = currencyRates!!.first()
            notifyPropertyChanged(BR.baseCurrency)
        }
    }

    /*** Булиевый флаг, который позволяет синхронизировать данные только тогда, когда
     * CurrencyConversionsActivity находится только в resumed state ***/
    private var isSyncing: Boolean = true

    /*** Синхронизация данных с интервалом SYNC_INTERVAL_MILLIS ***/
    private fun doDelaySync() {
        if (isSyncing) {
            getCurrencyRates()
            Handler().postDelayed( { doDelaySync() }, SYNC_INTERVAL_MILLIS)
        }
    }

    override fun onPause() {
        super.onPause()
        isSyncing = false
    }

    override fun onResume() {
        super.onResume()

        setCurrencyQuantity.subscribe { baseCurrencyText ->
            val valConvertedFromText = baseCurrencyText.replace(" ", "").toDoubleOrNull()
            baseCurrencyQuantity = if (valConvertedFromText != null) {
                valConvertedFromText
            } else {
                val splitValue = baseCurrencyText.split(",", ".").firstOrNull()
                splitValue?.toDoubleOrNull() ?: 0.toDouble()
            }
        }
        .addTo(disposable)

        isSyncing = true
        doDelaySync()
    }
}