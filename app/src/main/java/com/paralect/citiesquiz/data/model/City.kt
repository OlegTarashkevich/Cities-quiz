package com.paralect.citiesquiz.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class City(@SerializedName("capitalCity") var capitalCity: String? = null,
           var coordinate: LatLng? = null){
}