package com.newfarm.rates_and_conversions.presentationLayer.model

import com.google.gson.annotations.Expose
import com.newfarm.rates_and_conversions.R
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import kotlin.random.Random

open class Currency : RealmObject() {

    @PrimaryKey
    @Expose
    var name: String? = null

    @Expose
    var rate: Double? = null

    var isBase: Boolean = false

    @Ignore
    var currencyImage: Int? = null
        get() {
            if (field == null) {
                val currencyIcons = listOf(
                    R.drawable.ic_canada,
                    R.drawable.ic_us_dollar,
                    R.drawable.ic_euro,
                    R.drawable.ic_sweden)

                /*** Drawable resource is set randomly ***/
                field = currencyIcons.random(Random(this.hashCode() % 4))
            }
            return field
        }

    override fun equals(other: Any?): Boolean {
        return name == (other as? Currency)?.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}