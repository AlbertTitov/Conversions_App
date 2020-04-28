package com.newfarm.rates_and_conversions.presentationLayer.view.activity.common

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.newfarm.rates_and_conversions.R
import com.newfarm.rates_and_conversions.extentions.getMessage
import com.newfarm.rates_and_conversions.presentationLayer.view.interfaces.IDialog
import com.newfarm.rates_and_conversions.presentationLayer.viewModel.common.RootViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

abstract class DataBindingActivity<B: ViewDataBinding, VM: RootViewModel> : AppCompatActivity(), IDialog {
    protected lateinit var viewModel:VM
    protected lateinit var binding:B
        private set

    private var dialogBox: MaterialDialog? = null

    protected val disposable = CompositeDisposable()
    protected var isStarting = true
        private set

    abstract fun getLayoutResourceId() : Int
    abstract fun onBinding(binding: B)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = DataBindingUtil.setContentView(this, getLayoutResourceId())

        onInit()

        viewModel.title.subscribe {
            title = it
        }.addTo(disposable)

        viewModel.network.subscribe {
            if (it) {
                showProgressDialog()
            }
            else {
                hideProgressDialog()
            }
        }.addTo(disposable)

        viewModel.error.subscribe {
            hideProgressDialog()
            Toast.makeText(this, it.getMessage(), Toast.LENGTH_SHORT).show()
        }.addTo(disposable)

        viewModel.onStart(this)

        onBinding(this.binding)
    }

    open fun onInit() {

    }

    open fun onLoad() {

    }

    override fun onDestroy() {
        hideProgressDialog()
        Log.d(this.javaClass.simpleName, String.format("Disposed %d items", disposable.size()))
        viewModel.onStop()
        disposable.clear()
        super.onDestroy()
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        
        if (isStarting) {
            onLoad()
            isStarting = false
        }

        viewModel.onResume()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.onRestart()
    }

    override fun showProgressDialog(message: String) {
        hideProgressDialog()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dialogBox = MaterialDialog.Builder(this)
                .content(if (message.isEmpty()) getString(R.string.message_please_wait) else message)
                .progress(true, 0)
                .show()
    }
    override fun hideProgressDialog() {
        if (dialogBox != null) {
            dialogBox?.dismiss()
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}