package com.paralect.citiesquiz.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class City(@SerializedName("capitalCity") var capitalCity: String? = null,
           var latitude: Double? = 0.0,
           var longitude: Double? = 0.0)