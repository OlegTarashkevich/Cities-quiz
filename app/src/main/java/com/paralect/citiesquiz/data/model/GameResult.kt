package com.paralect.citiesquiz.data.model

import com.paralect.citiesquiz.presenter.GamePresenter.Companion.START_DISTANCE

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
data class GameResult(var totalDistance: Float = 0f,
                      var correctCities: Int = 0,
                      var totalCities: Int = 0,
                      var isFinished: Boolean = false) {

    fun addDistance(distance: Float) {
        totalDistance = totalDistance.plus(distance)
    }

    fun addCorrectCities() {
        correctCities = correctCities.plus(1)
    }

    fun addToTotalCities() {
        totalCities = totalCities.plus(1)
    }

    fun clear() {
        totalDistance = 0f
        correctCities = 0
        totalCities = 0
        isFinished = false
    }

    fun getRealDistance(): String {
        var value = START_DISTANCE - totalDistance
        if (value < 0)
            value = 0f
        return String.format("%.2f", value)
    }
}