package com.newfarm.rates_and_conversions.presentationLayer.viewModel.common

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import io.realm.RealmConfiguration

open class RootViewModel : BaseObservable {

    constructor() : super()

    protected val TAG = this.javaClass.simpleName

    val realm: Realm = Realm.getDefaultInstance()
    val realmInMemory: Realm = Realm.getInstance(RealmConfiguration.Builder().name("temp.realm").inMemory().build())

    lateinit var context: Context
    var fragment: Fragment? = null

    var title: PublishSubject<String> = PublishSubject.create()
    var network: PublishSubject<Boolean> = PublishSubject.create()
    var error: PublishSubject<Throwable> = PublishSubject.create()

    val disposable = CompositeDisposable()

    open fun onStart(context: Context) {
        Log.d(TAG, "onStart")
        this.context = context
    }
    open fun onStartFragment(context: Context, fragment: Fragment) {
        Log.d(TAG, "onStart")
        this.fragment = fragment
        onStart(context)
    }
    open fun onStop() {
        realm.close()
        realmInMemory.close()
        disposable.clear()
        Log.d(TAG, """onStop disposable - ${disposable.size()}""")
    }
    open fun onPause() {
        Log.d(TAG, "onPause")
    }
    open fun onResume() {
        Log.d(TAG, "onResume")
    }
    open fun onRestart() {
        Log.d(TAG, "onRestart")
    }
}