package com.newfarm.rates_and_conversions.helpers

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun formatCurrency (amount : Double?) : String {
    if (amount == null) {
        return ""
    }
    val symbols = DecimalFormatSymbols()
    symbols.groupingSeparator = ' '
    symbols.decimalSeparator = '.'

    val decimalFormat = DecimalFormat("#,###", symbols)
    decimalFormat.roundingMode = RoundingMode.CEILING
    return decimalFormat.format(amount)
}