package com.paralect.citiesquiz.presenter

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
interface IGamePresenter {

    fun setGameView(view: IGameView)

    fun loadGame()
}
