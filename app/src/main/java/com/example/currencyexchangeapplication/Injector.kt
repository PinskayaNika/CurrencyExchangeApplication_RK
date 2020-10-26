package com.example.currencyexchangeapplication

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class Injector(apiKey : String) {
    private val baseUrl = "https://min-api.cryptocompare.com/"

    val kodeIn = Kodein {
        bind<Service>() with singleton {
            Retrofit.Builder()
                .addCallAdapterFactory(instance())
                .addConverterFactory(instance())
                .client(instance())
                .baseUrl(baseUrl)
                .build()
                .create(Service::class.java)
        }
        bind<Interceptor>() with singleton { ApiKeyInterceptor(apiKey) }
        bind<OkHttpClient>() with singleton { OkHttpClient.Builder().addInterceptor(instance()).build() }
        bind<RxJava2CallAdapterFactory>() with singleton { RxJava2CallAdapterFactory.createAsync() }
        bind<GsonConverterFactory>() with singleton { GsonConverterFactory.create() }
    }
    inline fun <reified T : Any> instance() = kodeIn.instance<T>()
}