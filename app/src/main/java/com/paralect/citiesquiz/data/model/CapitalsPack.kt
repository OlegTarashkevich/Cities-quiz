package com.paralect.citiesquiz.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class CapitalsPack(@SerializedName("capitalCities") var pack: List<City> = emptyList())