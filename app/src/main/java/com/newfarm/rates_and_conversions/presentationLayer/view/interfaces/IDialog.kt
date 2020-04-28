package com.newfarm.rates_and_conversions.presentationLayer.view.interfaces

interface IDialog {
    fun showProgressDialog(message: String = "")
    fun hideProgressDialog()
}