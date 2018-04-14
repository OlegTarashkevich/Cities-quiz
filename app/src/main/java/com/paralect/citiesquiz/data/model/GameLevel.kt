package com.paralect.citiesquiz.data.model

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class GameLevel(val details: String, val city: City) {
    var distance: Float? = null
    var correct: Boolean? = null
}