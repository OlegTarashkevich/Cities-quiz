package com.paralect.citiesquiz.presenter

import com.paralect.citiesquiz.data.model.GameLevel

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
interface IGameView {

    fun onLevelLoaded(level: GameLevel)

    fun onGameOver()

    fun onError(e: Throwable)
}
