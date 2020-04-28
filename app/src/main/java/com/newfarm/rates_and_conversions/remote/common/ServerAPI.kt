package com.newfarm.rates_and_conversions.remote.common

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

open class ServerAPI<S:Any>(private val service: KClass<S>) {

    private fun httpClientBuilder(timeout:Long = 60): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1))
        builder.readTimeout(timeout, TimeUnit.SECONDS)
        builder.connectTimeout(timeout, TimeUnit.SECONDS)
        builder.writeTimeout(timeout, TimeUnit.SECONDS)
        return builder
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
            Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) {
                    nextResponseBodyConverter.convert(value)
                }
                else null
        }
    }

    private fun createJsonConverter(): GsonConverterFactory {
        val gson =  GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
        return GsonConverterFactory.create(gson)
    }

    protected fun get(timeout:Long = 60): S {
        val client = httpClientBuilder(timeout)

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(createJsonConverter())

        val retrofit = builder.client(client.build()).build()
        return retrofit.create(service.java)
    }
}