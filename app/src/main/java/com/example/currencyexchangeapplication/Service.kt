package com.example.currencyexchangeapplication

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import com.example.currencyexchangeapplication.CryptoCurrencyByDay
import com.example.currencyexchangeapplication.CryptoCurrency
import retrofit2.http.Query
import io.reactivex.Single
import retrofit2.Call


//private val moshi = Moshi
//    .Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()
//
//private val retrofit = Retrofit
//    .Builder()
//    .baseUrl("https://min-api.cryptocompare.com/")
//    .client(customHttpClient())
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .build()
//
//private fun customHttpClient(): OkHttpClient {
//    val builder = OkHttpClient.Builder()
//
//    val adapter = moshi.adapter(CryptoCurrency::class.java)
//
//    val authRequest = Request
//        .Builder()
//        .url("https://accounts.spotify.com/api/token")
//        .post(FormBody.Builder().add("grant_type", "client_credentials").build())
//        .addHeader(
//            "Authorization",
//            "Basic ${BuildConfig.API_KEY}"
//        )
//        .build()
//
////    builder.addInterceptor { chain ->
////        val response = OkHttpClient().newCall(authRequest).execute()
////        val accessToken = adapter.fromJson(response.body!!.string())!!.cryptoCurrencyName
////
////        chain.proceed(
////            chain
////                .request()
////                .newBuilder()
////                .header("Authorization", "Bearer $accessToken")
////                .build()
////        )
////    }
//
//    return builder.build()
//}
//
//interface ApiService {
//    @GET("search?q=genre:%22metalcore%22&type=artist&limit=50")
//    suspend fun getCurse(): CryptoCurrency
//}
//
//object Api {
//    val Service: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
//}
interface Service {
    @GET("data/v2/histoday")
    fun getHistory(@Query("fsym") fsym: String,
                   @Query("tsym") tsym: String,
                   @Query("limit") limit: Int?): Call<Data.Answer>
}
//https://min-api.cryptocompare.com/data/v2/histoday ?fsym=BTC&tsym=USD&limit=4
