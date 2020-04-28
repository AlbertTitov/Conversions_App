package com.newfarm.rates_and_conversions.remote.response

import com.google.gson.annotations.Expose

class CurrencyRatesResponse {

	@Expose
	var baseCurrency: String? = null

	@Expose
	var rates: HashMap<String, Double>? = null
}