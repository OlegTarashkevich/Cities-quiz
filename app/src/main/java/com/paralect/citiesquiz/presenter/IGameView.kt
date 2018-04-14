package com.paralect.citiesquiz.presenter

import com.paralect.citiesquiz.data.model.GameLevel
import com.paralect.citiesquiz.data.model.GameResult

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
interface IGameView {

    fun onShowProgress(show: Boolean)

    fun onLevelLoaded(level: GameLevel)

    fun onGameResult(result: GameResult)

    fun onError(e: Throwable)
}
