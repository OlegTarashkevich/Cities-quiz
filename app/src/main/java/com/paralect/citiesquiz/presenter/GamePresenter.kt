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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class GamePresenter() : IGamePresenter {

    private val ALLOWED_DISTANCE = 50
    private val START_DISTANCE: Float = 1500f
    private var gameDistance: Float = START_DISTANCE
    private val levels: HashSet<GameLevel> = HashSet()

    private var mDataView: IGameView? = null
    private var cities: ArrayList<City> = ArrayList()

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
            val details = App.app.getString(R.string.task_details, city.capitalCity)
            val game = GameLevel(details, city)
            currentLevel = game
            mDataView?.onLevelLoaded(game)
        } else {
            loadGame()
        }
    }

    private fun generateGameResult() {
        Single.create<GameResult> {
            try {
                if (!it.isDisposed) {
                    var correctCities = 0
                    var totalDistance = 0f
                    levels.forEach {
                        if (it.correct) correctCities = correctCities.plus(1)
                        totalDistance = totalDistance.plus(it.distance)
                    }
                    val gameResult = GameResult(totalDistance, correctCities, levels.size)
                    it.onSuccess(gameResult)
                }
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mDataView?.onGameResult(it)
                    resetGame()
                }, { mDataView?.onError(it) })
    }

    fun isLoaded() = cities.isNotEmpty()

    private fun resetGame() {
        levels.clear()
        currentLevel = null
        gameDistance = START_DISTANCE
    }

    fun setUsersCoordinate(coordinate: LatLng) {
        if (currentLevel == null)
            mDataView?.onError(RuntimeException("Please reload the game"))

        currentLevel?.let {
            val distance = Utils.getDistance(coordinate, it.city.coordinate!!) / 1000
            it.distance = distance
            it.correct = it.distance <= ALLOWED_DISTANCE
            levels.add(it)
            gameDistance = gameDistance.minus(distance)
            if (gameDistance > 0)
                loadNextGameLevel()
            else
                generateGameResult()
        }
    }
}