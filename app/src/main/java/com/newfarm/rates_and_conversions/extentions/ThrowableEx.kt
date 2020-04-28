package com.newfarm.rates_and_conversions.extentions

import retrofit2.HttpException

fun Throwable.getMessage() : String {
    return when {
        this is HttpException -> {
            return when (this.code()) {
                401 -> "Неверный логин или пароль. Доступ запрещён."
                else -> this.message()
            }
        }
        this.localizedMessage != null -> this.localizedMessage!!
        this.message != null -> this.message!!
        else -> this.toString()
    }
}