package com.newfarm.rates_and_conversions.extentions

import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults

interface RealmCascadeDelete {
    fun cascadeDelete()
}

fun Realm.executeTransactionSafe(transaction: () -> Unit) {
    if (this.isInTransaction) {
        transaction()
    }
    else {
        this.executeTransaction { transaction() }
    }
}

fun RealmObject.deleteFromRealmEx() {
    if (this is RealmCascadeDelete) {
        cascadeDelete()
    }
    if (this.isValid) {
        Log.d("RealmEx", this.toString())
        this.deleteFromRealm()
    }
}

fun RealmResults<*>.deleteFromRealmEx() {
    val tmp = this.toArray()
    tmp.forEach {
        try {
            if (it is RealmObject) {
                if (it.isValid) {
                    it.deleteFromRealmEx()
                }
            }
        }
        catch (ex: Throwable) {
            Log.e("RealmEx", ex.toString())
        }
    }
    this.deleteAllFromRealm()
}

fun RealmList<*>.deleteFromRealmEx() {
    val tmp = this.toArray()
    tmp.forEach {
        try {
            if (it is RealmObject) {
                if (it.isValid) {
                    it.deleteFromRealmEx()
                }
            }
        }
        catch (ex: Throwable) {
            Log.e("RealmEx", ex.toString())
        }
    }
    this.deleteAllFromRealm()
}