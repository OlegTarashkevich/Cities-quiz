package com.paralect.citiesquiz.presenter

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.paralect.citiesquiz.App
import com.paralect.citiesquiz.R
import com.paralect.citiesquiz.data.DataUtil
import com.paralect.citiesquiz.data.model.CapitalsPack
import com.paralect.citiesquiz.data.model.City
import com.paralect.citiesquiz.data.model.GameLevel
import com.paralect.citiesquiz.data.model.GameResult
import com.paralect.citiesquiz.utils.Utils
import com.paralect.citiesquiz.utils.random
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class GamePresenter() : IGamePresenter {

    companion object {
        val START_DISTANCE: Float = 1500f
    }

    private val ALLOWED_DISTANCE = 50
    private var gameDistance: Float = START_DISTANCE

    private var mDataView: IGameView? = null
    private var cities: ArrayList<City> = ArrayList()

    private val gameResult = GameResult()
    var currentLevel: GameLevel? = null
        private set

    override fun setGameView(view: IGameView) {
        mDataView = view
    }

    override fun loadGame() {
//        if (isLoaded()) return
        if (!Utils.hasNetwork(App.app)) {
            mDataView?.onError(RuntimeException("Please enable network"))
        }

        resetGame()
        mDataView?.onShowProgress(true)
        DataUtil().getResponseFromResObservable(App.app, CapitalsPack::class.java, R.raw.capital_cities)
                .toFlowable()
                .flatMapIterable { it.pack }
                .map {
                    val geocoder = Geocoder(App.app, Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(it.capitalCity, 1)
                    val address = addresses[0]
                    it.coordinate = LatLng(address.latitude, address.longitude)
                    it
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    cities.clear()
                    cities.addAll(it)
                    loadNextGameLevel()
                    mDataView?.onShowProgress(false)
                }, { mDataView?.onError(it) })
    }

    private fun loadNextGameLevel() {
        if (isLoaded()) {
            val city = cities.random()!!
            val game = GameLevel("Find " + city.capitalCity + " on map", city)
            currentLevel = game
            mDataView?.onLevelLoaded(game, gameResult)
        } else {
            loadGame()
        }
    }

    fun isLoaded() = cities.isNotEmpty()

    private fun resetGame() {
        gameResult.clear()
        currentLevel = null
        gameDistance = START_DISTANCE
    }

    fun setUsersCoordinate(coordinate: LatLng) {
        if (gameResult.isFinished) {
            mDataView?.onError(RuntimeException("Please reload the game"))
            return
        }

        currentLevel?.let {
            val distance = Utils.getDistance(coordinate, it.city.coordinate!!) / 1000
            it.distance = distance
            it.correct = it.distance <= ALLOWED_DISTANCE

            if (it.correct) {
                gameResult.addCorrectCities()
                mDataView?.onMessage("Correct!")
            } else {
                mDataView?.onMessage("Incorrect!")
            }

            gameResult.addToTotalCities()
            gameResult.addDistance(it.distance)

            gameDistance = gameDistance.minus(distance)
            if (gameDistance > 0)
                loadNextGameLevel()
            else {
                gameResult.isFinished = true
                mDataView?.onGameResult(gameResult)
            }
        }
    }

    fun getRealCoordinate() = currentLevel?.city?.coordinate
}