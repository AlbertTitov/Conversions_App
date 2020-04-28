package com.newfarm.rates_and_conversions

import android.app.Application
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        Realm.getDefaultInstance()

        Log.d("App", "Initialize")
    }

    companion object {
        var instance: App? = null
    }
}