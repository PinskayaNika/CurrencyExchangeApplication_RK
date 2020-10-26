package com.example.currencyexchangeapplication

import com.google.gson.annotations.SerializedName

class Data {
    data class Answer (
        @SerializedName("Response") var response: String,
        @SerializedName("Type") var type: Int,
        @SerializedName("Data") var days: Day
    )

    data class Day (
        @SerializedName("TimeFrom") var timeFrom: String,
        @SerializedName("TimeTo") var timeTo: String,
        @SerializedName("Data") var records: List<Record>
    )

    data class Record (
        @SerializedName("time") var time: Long,
        @SerializedName("high") var high: Float,
        @SerializedName("low") var low: Float,
        @SerializedName("open") var open: Float,
        @SerializedName("volumefrom") var volumeFrom: Float,
        @SerializedName("volumeto") var volumeTo: Float,
        @SerializedName("close") var close: Float
    )


}